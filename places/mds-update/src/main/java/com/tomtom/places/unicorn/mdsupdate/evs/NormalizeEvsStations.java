package com.tomtom.places.unicorn.mdsupdate.evs;

import static com.tomtom.places.commons.ttom.AttributeUtils.getDictionaryRangeItemAttributeValue;
import static com.tomtom.places.commons.ttom.AttributeUtils.getDictionaryRangeItemSetAttributeValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.teleatlas.global.common.ddct.DictionaryProperty;
import com.teleatlas.models.ttom.ttom_poi.TTOM_POI;
import com.teleatlas.models.ttom.ttom_poi.feature.DDCTChargingSpotInformation.DDCTChargingSpotInformationProperty;
import com.tomtom.common.serdeser.Deserializer;
import com.tomtom.common.serdeser.json.JsonDeserializer;
import com.tomtom.cpu.api.features.Attribute;
import com.tomtom.cpu.api.features.Feature;
import com.tomtom.cpu.api.features.NonSpatialObject;
import com.tomtom.cpu.api.geometry.Geometry;
import com.tomtom.cpu.coredb.client.interfaces.Branch;
import com.tomtom.cpu.coredb.client.interfaces.CheckException;
import com.tomtom.cpu.coredb.client.interfaces.Version;
import com.tomtom.cpu.coredb.mutable.MutableAttribute;
import com.tomtom.cpu.coredb.mutable.MutableObjectManipulator;
import com.tomtom.cpu.coredb.quality.CheckResults;
import com.tomtom.mau.authtoken.AuthenticationManager;
import com.tomtom.mau.authtoken.AuthenticationServiceAccess;
import com.tomtom.places.commons.ttom.AttributeUtils;
import com.tomtom.places.unicorn.coredb.CommitterController;
import com.tomtom.places.unicorn.coredb.CoreDBClient;
import com.tomtom.places.unicorn.coredb.CoreDBClientWithCommitterController;
import com.tomtom.places.unicorn.coredb.CoreDBConnection;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlaceDiff;
import com.tomtom.places.unicorn.domain.avro.archive.RelatedArchivePlaceDiff;
import com.tomtom.places.unicorn.migration.util.AvroFileWriter;
import com.tomtom.places.unicorn.migration.util.CoreDBClientManager;
import com.tomtom.places.unicorn.model.SingletonModelFactory;
import com.tomtom.places.unicorn.ttom.TtomToArchive;
import com.tomtom.places.unicorn.ttom.util.TtomPrinter;
import com.tomtom.qa.qa_io.response.QAResponse;
import com.tomtom.qa.qa_io.response.QAResponseImpl;
import com.tomtom.qa.qa_io.violation.Violation;

public class NormalizeEvsStations {

    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizeEvsStations.class);

    private static final TtomToArchive ttomToArchive = TtomToArchive.newTtomToArchive();
    private static final TtomToArchive ttomToArchiveNoAssoc = TtomToArchive.newTtomToArchiveWithoutAssociations();

    private static final TTOM_POI TTOM_POI = SingletonModelFactory.getTtomPoi();
    private static final MutableObjectManipulator MUTABLE_FEATURE_MANIPULATOR = new MutableObjectManipulator();
    // private static final TtomPrinter TTOM_PRINTER = TtomPrinter.toSystemOut();
    // private static final TtomPrinter TTOM_PRINTER_NO_ASSOC = TtomPrinter.toSystemOut().withAssociations(false);

    private static final DictionaryProperty NUMBER_OF_EV_CHARGING_SPOTS =
        TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation.NumberOfEVChargingSpots;
    private static final DictionaryProperty CHARGING_STATION_IDENTIFIER =
        TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation.ChargingStationIdentifier;
    private static final DDCTChargingSpotInformationProperty COMPOSITE_CHARGING_SPOT =
        TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation.CompositeChargingSpot;
    private static final DictionaryProperty E_V_CHARGING_RECEPTACLE_TYPE = COMPOSITE_CHARGING_SPOT.EVChargingReceptacleType;
    private static final DictionaryProperty E_V_CHARGING_FACILITIES = COMPOSITE_CHARGING_SPOT.EVChargingFacilities;
    private static final DictionaryProperty CHARGING_SPOT_SERVICES = COMPOSITE_CHARGING_SPOT.ChargingSpotServices;

    private CommandLineParams cmdLineParams;
    private FalloutWriter updatedWriter;
    private FalloutWriter errorsWriter;
    private FalloutWriter retryWriter;

    private AvroFileWriter<RelatedArchivePlaceDiff> deltaWriter;

    public NormalizeEvsStations(String args[]) throws CmdLineException, IOException {
        cmdLineParams = new CommandLineParams();
        CmdLineParser parser = new CmdLineParser(cmdLineParams);
        parser.parseArgument(args);

        updatedWriter = new FalloutWriter(cmdLineParams.inputFile + ".updated");
        errorsWriter = new FalloutWriter(cmdLineParams.inputFile + ".errors");
        retryWriter = new FalloutWriter(cmdLineParams.inputFile + ".retry");
        deltaWriter = new AvroFileWriter<RelatedArchivePlaceDiff>(cmdLineParams.inputFile + ".delta.avro",
            RelatedArchivePlaceDiff.SCHEMA$, RelatedArchivePlaceDiff.class);
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("Arguments: " + Arrays.toString(args));
        NormalizeEvsStations normalizer = new NormalizeEvsStations(args);
        normalizer.normalizeStations();
    }

    public void normalizeStations() throws Exception {
        CoreDBClient coreDB = createCoreDBClient();
        coreDB.connect();
        try {
            Branch branch = coreDB.getCurrentBranch();

            List<String> lines = FileUtils.readLines(new File(cmdLineParams.inputFile), Charset.forName("UTF-8"));
            for (String line : lines) {
                Thread.currentThread().setName(line);
                Iterator<String> parts = Splitter.on("|").split(line).iterator();
                String featureId = parts.next();
                String externalId = parts.hasNext() ? parts.next() : "";
                normalizeStation(featureId, externalId, coreDB, branch);
            }
            LOGGER.info("Done.");
        } finally {
            CoreDBClientManager.disconnect(coreDB);
            IOUtils.closeQuietly(deltaWriter);
            System.exit(0);
        }
    }

    private void normalizeStation(String featureId, String externalId, CoreDBClient coreDB, Branch branch) throws IOException {
        String featureIdExternalId = featureId + "|" + externalId;
        try {
            LOGGER.info("Querying: " + featureIdExternalId);
            Feature<? extends Geometry> feature = getFeature(featureId, coreDB, branch);

            if (hasEvsStationWithMultipleSpots(feature)) {

                StringWriter out = getFeatureTTomString(feature);
                LOGGER.info("Original feature: " + featureIdExternalId + "\t" + out.toString());

                if (feature != null && feature.getGeometry() != null) {
                    Feature<? extends Geometry> mutableFeature = MUTABLE_FEATURE_MANIPULATOR.createMutableFeature(feature);
                    Collection<Attribute<?>> stationAttrs =
                        mutableFeature.getAttributes(TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation);
                    List<Attribute<?>> normalizedStations = getNormalizedStations(stationAttrs);
                    MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes(mutableFeature, normalizedStations);

                    writeDelta(feature, mutableFeature);

                } else {
                    errorsWriter.writeFallout(featureIdExternalId + "||Could not find the feature");
                }
            } else {
                LOGGER.info("EVS stations does not contain any repetative spots.");
            }
        } catch (Exception exception) {
            LOGGER.info("ERROR: ", exception);
            errorsWriter.writeFallout(featureIdExternalId + "||" + exception.getMessage());
        }
    }

    public boolean hasEvsStationWithMultipleSpots(Feature<? extends Geometry> feature) {
        boolean result = false;

        Collection<NonSpatialObject> chargingStations =
            AttributeUtils.getCompositeAttributeValues(feature, TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation);
        if (!chargingStations.isEmpty()) {
            for (NonSpatialObject chargingStation : chargingStations) {
                Collection<NonSpatialObject> spots =
                    AttributeUtils.getCompositeAttributeValues(chargingStation, COMPOSITE_CHARGING_SPOT);

                if (spots != null && spots.size() > 1) {
                    Set<String> spotAttributes = Sets.newHashSet();
                    for (NonSpatialObject spot : spots) {
                        Integer chargingSpotServices = getDictionaryRangeItemSetAttributeValue(spot, CHARGING_SPOT_SERVICES);
                        Integer chargingFacilities = getDictionaryRangeItemSetAttributeValue(spot, E_V_CHARGING_FACILITIES);
                        Integer receptacleType = getDictionaryRangeItemAttributeValue(spot, E_V_CHARGING_RECEPTACLE_TYPE, Integer.class);
                        String key = chargingSpotServices + "_" + chargingFacilities + "_" + receptacleType;
                        if (false == spotAttributes.add(key)) {
                            // key already present means spot with same attribute already present
                            LOGGER.info("Spot key repetative: [" + key + "]\t");
                            result = true;
                        }
                    }
                    result = true;
                }
            }
        }
        return result;
    }

    private StringWriter getFeatureTTomString(Feature<? extends Geometry> feature) {
        StringWriter out = new StringWriter();
        TtomPrinter printer = TtomPrinter.to(new PrintWriter(out)).withAssociations(false);
        printer.printFeature(feature);
        return out;
    }

    private void writeDelta(Feature<? extends Geometry> beforeFeature, Feature<? extends Geometry> afterFeature) throws IOException {
        StringWriter out = getFeatureTTomString(afterFeature);
        LOGGER.info("Modified feature: " + beforeFeature.getId() + "\t" + out.toString());

        ArchivePlace beforePlace = ttomToArchive.poiToFeature(beforeFeature);
        ArchivePlace afterPlace = ArchivePlace.newBuilder(beforePlace).build();
        ArchivePlace afterPlaceWithoutAssoc = ttomToArchiveNoAssoc.poiToFeature(afterFeature);
        afterPlace.setAttributes(afterPlaceWithoutAssoc.getAttributes());

        if (beforePlace.equals(afterPlace)) {
            LOGGER.info("EVS station data is already correct. No Delta need to be created.");
        } else {
            ArchivePlaceDiff archivePlaceDiff = new ArchivePlaceDiff(beforePlace, afterPlace);
            RelatedArchivePlaceDiff delta =
                RelatedArchivePlaceDiff.newBuilder().setArchivePlaceDiffs(Lists.newArrayList(archivePlaceDiff)).build();
            deltaWriter.write(delta);
            deltaWriter.flush();
        }
    }

    private List<Attribute<?>> getNormalizedStations(Collection<Attribute<?>> stationAttrs) {
        List<Attribute<?>> normalizedStations = Lists.newArrayList();
        int stationCounter = 0;
        for (Attribute<?> stationAttr : stationAttrs) {
            stationCounter++;
            Map<EvsSpotKey, EvsStation> stationMap = prepareStationsMap(stationAttr, stationCounter);
            Set<Entry<EvsSpotKey, EvsStation>> entrySet = stationMap.entrySet();
            for (Entry<EvsSpotKey, EvsStation> stationEntry : entrySet) {
                // for each unique spot key MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute of station
                // and replace the spots in station with unique spot combination value
                LOGGER.info(stationEntry.getKey() + "\t" + stationEntry.getValue().getStationAttr().getValue());
                MutableAttribute normalizedStation = getNormalizedStation(stationAttr, stationEntry);
                normalizedStations.add(normalizedStation);
            }
        }
        return normalizedStations;
    }

    private MutableAttribute getNormalizedStation(Attribute<?> stationAttr, Entry<EvsSpotKey, EvsStation> stationEntry) {
        MutableAttribute normalizedStation = MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(stationAttr);

        NonSpatialObject station = (NonSpatialObject)stationAttr.getValue();
        Integer currSpotsCount = AttributeUtils.getSingleSimpleAttributeValue(station, NUMBER_OF_EV_CHARGING_SPOTS, Integer.class);

        if (stationEntry.getValue().getSpotCount() > 1) {
            // station has more than one spot instances. update the spot count == number of instances found
            MutableAttribute newSpotCount = getUpdatedSpotCountAttribute(stationEntry, station);
            MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes((NonSpatialObject)normalizedStation.getValue(),
                Lists.<Attribute<?>>newArrayList(newSpotCount));
        } else {
            // this means only one spot is present in existing station
            // in this case keep the existing count
            LOGGER.info("Keeping the current spot count: " + currSpotsCount);
        }

        MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes((NonSpatialObject)normalizedStation.getValue(),
            Lists.<Attribute<?>>newArrayList(stationEntry.getValue().getSpotAttr()));
        return normalizedStation;
    }

    private MutableAttribute getUpdatedSpotCountAttribute(Entry<EvsSpotKey, EvsStation> stationEntry, NonSpatialObject station) {
        Collection<Attribute<?>> numOfChargingSpots = station.getAttributes(NUMBER_OF_EV_CHARGING_SPOTS);
        Attribute<?> spotCountAttr = Iterables.getFirst(numOfChargingSpots, null);

        MutableAttribute newSpotCount = MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(spotCountAttr);
        newSpotCount.setValue(stationEntry.getValue().getSpotCount());
        return newSpotCount;
    }

    private static Map<EvsSpotKey, EvsStation> prepareStationsMap(Attribute<?> stationAttr, int stationCounter) {
        NonSpatialObject station = (NonSpatialObject)stationAttr.getValue();

        Map<EvsSpotKey, EvsStation> stationMap = Maps.newLinkedHashMap();
        String stationId =
            AttributeUtils.getSingleSimpleAttributeValue(station, CHARGING_STATION_IDENTIFIER, String.class);
        Collection<Attribute<?>> spotAttrs = station.getAttributes(COMPOSITE_CHARGING_SPOT);

        int spotCounter = 0;
        for (Attribute<?> spotAttr : spotAttrs) {
            spotCounter++;
            NonSpatialObject spot = (NonSpatialObject)spotAttr.getValue();
            EvsSpotKey spotKey = getSpotKey(stationId, spot);
            LOGGER.info(stationId + "\t" + stationCounter + "\t" + spotCounter + "\t" + spotKey + "\t" + spot.getType());

            if (stationMap.containsKey(spotKey)) {
                stationMap.get(spotKey).incrementSpotCount();
            } else {
                stationMap.put(spotKey, new EvsStation(spotKey, stationAttr, spotAttr));
            }
        }
        return stationMap;
    }

    private static EvsSpotKey getSpotKey(String stationId, NonSpatialObject spot) {
        Integer chargingSpotServicesBitmask =
            AttributeUtils.getDictionaryRangeItemSetAttributeValue(spot, CHARGING_SPOT_SERVICES);
        Integer chargingFacilitiesBitmask =
            AttributeUtils.getDictionaryRangeItemSetAttributeValue(spot, E_V_CHARGING_FACILITIES);
        Integer receptacleType =
            AttributeUtils.getDictionaryRangeItemAttributeValue(spot, E_V_CHARGING_RECEPTACLE_TYPE, Integer.class);

        return new EvsSpotKey(stationId, chargingSpotServicesBitmask, chargingFacilitiesBitmask, receptacleType);
    }

    private static Feature<? extends Geometry> getFeature(String featureId, CoreDBClient coreDB, Branch branch) {
        Version version = coreDB.getCurrentVersion(branch);
        Feature<? extends Geometry> feature =
            coreDB.getFeatureById(featureId, version.getJournalVersion(), branch.getBranchId().toString());
        return feature;
    }

    private void update(String line, Feature<? extends Geometry> mutableFeature) throws IOException {
        if (mutableFeature != null) {
            // updateFeature(coreDB, mutableFeature, line);

            // LOGGER.info("===============================================");
            // TTOM_PRINTER.printFeature(feature);
            // LOGGER.info("===============================================");
            // TTOM_PRINTER.printFeature(mutableFeature);
            // LOGGER.info("===============================================");

        } else {
            errorsWriter.writeFallout(line + "||Evs station data in feature is ok");
        }
    }

    private void printStations(Collection<Attribute<?>> stations) {
        int stationCounter = 0;
        for (Attribute<?> stationAttr : stations) {
            stationCounter++;
            NonSpatialObject station = (NonSpatialObject)stationAttr.getValue();
            Collection<Attribute<?>> spotAttrs = station.getAttributes(COMPOSITE_CHARGING_SPOT);

            int spotCounter = 0;

            for (Attribute<?> spotAttr : spotAttrs) {
                spotCounter++;
                NonSpatialObject spot = (NonSpatialObject)spotAttr.getValue();
                LOGGER.info(stationCounter + "\t" + spotCounter + "\t" + spot.getType());

                Collection<Attribute<?>> recpt = spot.getAttributes(E_V_CHARGING_RECEPTACLE_TYPE);
                for (Attribute<?> attribute : recpt) {
                    LOGGER.info("\t" + attribute.getValue());
                }

                Collection<Attribute<?>> faci = spot.getAttributes(E_V_CHARGING_FACILITIES);
                for (Attribute<?> attribute : faci) {
                    LOGGER.info("\t" + attribute.getValue());
                }

                Collection<Attribute<?>> srvs = spot.getAttributes(CHARGING_SPOT_SERVICES);
                for (Attribute<?> attribute : srvs) {
                    LOGGER.info("\t" + attribute.getValue());
                }

                // for each unique spot MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute of station
                // and replace the spots in station with unique spot combination value
            }
        }
    }

    private static Feature<? extends Geometry> normalizeStations(Feature<? extends Geometry> feature) {
        int stationCounter = 0;
        Collection<NonSpatialObject> chargingStations =
            AttributeUtils.getCompositeAttributeValues(feature, TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation);
        for (NonSpatialObject station : chargingStations) {
            stationCounter++;
            Collection<NonSpatialObject> spots = AttributeUtils.getCompositeAttributeValues(station, COMPOSITE_CHARGING_SPOT);

            int spotCounter = 0;
            for (NonSpatialObject spot : spots) {
                spotCounter++;
                LOGGER.info(stationCounter + "\t" + spotCounter + "\t" + station + "\t" + spot);
            }
        }
        LOGGER.info("------------------");
        return null;
    }

    private static void printStationsAndSpots(Feature<? extends Geometry> feature) {
        int stationCounter = 0;
        Collection<NonSpatialObject> chargingStations =
            AttributeUtils.getCompositeAttributeValues(feature,
                TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation);
        for (NonSpatialObject station : chargingStations) {
            stationCounter++;
            Collection<NonSpatialObject> spots = AttributeUtils.getCompositeAttributeValues(station,
                TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation.CompositeChargingSpot);

            int spotCounter = 0;
            for (NonSpatialObject spot : spots) {
                spotCounter++;
                LOGGER.info(stationCounter + "\t" + spotCounter + "\t" + station + "\t" + spot);
            }
        }
        LOGGER.info("------------------");
    }

    private void updateFeature(CoreDBClient coreDB, Feature<? extends Geometry> mutableFeature, String line) throws IOException {

        String txnId = "";
        try {
            coreDB.beginTransaction();
            txnId = coreDB.getCurrentTransactionId();
            coreDB.setTransactionMetaData(null, getMetadata());
            coreDB.updateFeature(mutableFeature);

            // CheckResults checkResult = coreDB.checkTransaction();
            // LOGGER.info("QA Response: " + getQAResponse(checkResult));
            // coreDB.rollbackTransaction();

            coreDB.commitTransactionWithoutRollback();

            updatedWriter.writeFallout(line + "|" + txnId);
            LOGGER.info("Updated feature: " + mutableFeature.getId() + ", Transaction: " + txnId);

        } catch (CheckException exception) {
            QAResponse qaResponse = parseQaResponse(exception.getDetails());
            Set<Violation> violations = qaResponse.getViolations();
            List<String> violationMessages = Lists.newArrayList(Iterables.transform(violations, new Function<Violation, String>() {

                public String apply(Violation input) {
                    return input.getMessage();
                }
            }));
            errorsWriter.writeFallout(line + "|" + txnId + "|Violations: " + violationMessages.toString());
        } catch (Exception exception) {
            retryWriter.writeFallout(line + "|" + txnId + "|" + exception.getMessage());
            try {
                if (txnId != null) {
                    coreDB.rollbackTransaction();
                }
            } catch (Exception ex) {
            }
        }
    }

    private String getQAResponse(CheckResults checkResult) {
        // LOGGER.info(checkResult.getQaResponse());
        QAResponse qaResponse = parseQaResponse(checkResult.getQaResponse());
        Set<Violation> violations = qaResponse.getViolations();
        List<String> violationMessages = Lists.newArrayList(Iterables.transform(violations, new Function<Violation, String>() {

            public String apply(Violation input) {
                return input.getMessage();
            }
        }));
        return checkResult.getCheckStatus() + (violationMessages.size() > 0 ? " : " + violationMessages : "");
    }

    private CoreDBClient createCoreDBClient() {
        CoreDBConnection coreDbConnection = new CoreDBConnection(cmdLineParams.coreDbUrl);
        // return new CoreDBClientImpl(coreDbConnection);
        AuthenticationServiceAccess authServiceAccess =
            new AuthenticationServiceAccess(true, cmdLineParams.authServerUrl, "atm", "atmsecret");
        AuthenticationManager authenticationManager = new AuthenticationManager(authServiceAccess);
        CommitterController committerController = new CommitterController(cmdLineParams.commitControllerUrl, "ArchiveToMDS");
        return new CoreDBClientWithCommitterController(coreDbConnection, cmdLineParams.accessPointUrl, cmdLineParams.baselineName,
            cmdLineParams.baselineVersion, committerController, authenticationManager, false, true, false);
    }

    private Map<String, String> getMetadata() {
        Map<String, String> metadataParam = Maps.newHashMap();
        metadataParam.put("ToolName", "POI BrandName NC");
        metadataParam.put("ToolVersion", "1.0");
        metadataParam.put("UserID", cmdLineParams.userName);
        return metadataParam;
    }

    private QAResponse parseQaResponse(String rawQaResponse) {
        QAResponse qaResponse = null;

        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(rawQaResponse, "UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Deserializer<QAResponseImpl> jsonDeserializer = new JsonDeserializer<QAResponseImpl>(QAResponseImpl.class, objectMapper);
            qaResponse = jsonDeserializer.deserialize(inputStream);
        } catch (Exception ignored) {
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return qaResponse;
    }
}
