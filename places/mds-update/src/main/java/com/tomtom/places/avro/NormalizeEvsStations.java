package com.tomtom.places.avro;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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

import cern.colt.Arrays;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teleatlas.global.common.ddct.DictionaryModelStoreFactory;
import com.teleatlas.global.common.ddct.DictionaryProperty;
import com.teleatlas.models.ttom.TTOM;
import com.teleatlas.models.ttom.ttom_name.TTOM_Name;
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
import com.tomtom.places.unicorn.model.SingletonModelFactory;
import com.tomtom.qa.qa_io.response.QAResponse;
import com.tomtom.qa.qa_io.response.QAResponseImpl;
import com.tomtom.qa.qa_io.violation.Violation;

public class NormalizeEvsStations {

    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizeEvsStations.class);

    private static final TTOM_POI TTOM_POI = SingletonModelFactory.getTtomPoi();
    private static final TTOM TTOM = new TTOM(DictionaryModelStoreFactory.getModelStore());
    private static final TTOM_Name TTOM_NAME = TTOM.TTOM_Name;
    private static final MutableObjectManipulator MUTABLE_FEATURE_MANIPULATOR = new MutableObjectManipulator();
    private static final TtomPrinter TTOM_PRINTER = TtomPrinter.toSystemOut().withAssociations(false);

    private static final DictionaryProperty NUMBER_OF_EV_CHARGING_SPOTS =
        TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation.NumberOfEVChargingSpots;
    private static final DictionaryProperty CHARGING_STATION_IDENTIFIER =
        TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation.ChargingStationIdentifier;
    private static final DDCTChargingSpotInformationProperty COMPOSITE_CHARGING_SPOT =
        TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation.CompositeChargingSpot;
    private static final DictionaryProperty E_V_CHARGING_RECEPTACLE_TYPE = COMPOSITE_CHARGING_SPOT.EVChargingReceptacleType;
    private static final DictionaryProperty E_V_CHARGING_FACILITIES = COMPOSITE_CHARGING_SPOT.EVChargingFacilities;
    private static final DictionaryProperty CHARGING_SPOT_SERVICES = COMPOSITE_CHARGING_SPOT.ChargingSpotServices;

    private static CommandLineOptions CONFIG;
    private static FalloutWriter updatedWriter;
    private static FalloutWriter errorsWriter;
    private static FalloutWriter retryWriter;

    public static void main(String[] args) throws Exception {
        CONFIG = new CommandLineOptions();
        CmdLineParser parser = new CmdLineParser(CONFIG);
        try {
            parser.parseArgument(args);
            LOGGER.info("Arguments: " + Arrays.toString(args));

            updatedWriter = new FalloutWriter(CONFIG.inputFile + ".updated");
            errorsWriter = new FalloutWriter(CONFIG.inputFile + ".errors");
            retryWriter = new FalloutWriter(CONFIG.inputFile + ".retry");

            List<String> lines = FileUtils.readLines(new File(CONFIG.inputFile), Charset.forName("UTF-8"));

            CoreDBClient coreDB = createCoreDBClient();
            coreDB.connect();
            Branch branch = coreDB.getCurrentBranch();
            for (String line : lines) {
                try {
                    Iterator<String> parts = Splitter.on("|").split(line).iterator();
                    String featureId = parts.next();
                    String externalId = parts.hasNext() ? parts.next() : "";
                    System.out.println("******************************************************************************");
                    System.out.println("Querying: " + line);
                    Feature<? extends Geometry> feature = getFeature(featureId, coreDB, branch);

                    if (feature != null && feature.getGeometry() != null) {
                        TTOM_PRINTER.printFeature(feature);

                        // Feature<? extends Geometry> mutableFeature = normalizeStations(feature);
                        // printStationsAndSpots(feature);
                        Feature<? extends Geometry> mutableFeature = MUTABLE_FEATURE_MANIPULATOR.createMutableFeature(feature);
                        Collection<Attribute<?>> stationAttrs =
                            mutableFeature.getAttributes(TTOM_POI.FEATURES.ElectricVehicleStation.CompositeChargingStation);

                        List<Attribute<?>> newStations = Lists.newArrayList();

                        int stationCounter = 0;
                        for (Attribute<?> stationAttr : stationAttrs) {
                            stationCounter++;
                            NonSpatialObject station = (NonSpatialObject)stationAttr.getValue();

                            Map<EvsSpotKey, EvsStation> stationMap = prepareStationsMap(stationAttr, stationCounter);

                            // for each unique spot key MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute of station
                            // and replace the spots in station with unique spot combination value
                            Set<Entry<EvsSpotKey, EvsStation>> entrySet = stationMap.entrySet();
                            for (Entry<EvsSpotKey, EvsStation> newStation : entrySet) {
                                System.out.println(newStation.getKey() + "\t" + newStation.getValue().getStationAttr().getValue());

                                MutableAttribute mutableStation = MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(stationAttr);

                                Collection<Attribute<?>> numOfChargingSpots = station.getAttributes(NUMBER_OF_EV_CHARGING_SPOTS);
                                Attribute<?> spotCountAttr = Iterables.getFirst(numOfChargingSpots, null);

                                MutableAttribute newSpotCount = MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(spotCountAttr);
                                newSpotCount.setValue(newStation.getValue().getSpotCount());

                                TTOM_PRINTER.printAttribute(mutableStation, 5);

                                MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes((NonSpatialObject)mutableStation.getValue(),
                                    Lists.<Attribute<?>>newArrayList(newStation.getValue().getSpotAttr()));

                                MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes((NonSpatialObject)mutableStation.getValue(),
                                    Lists.<Attribute<?>>newArrayList(newSpotCount));

                                newStations.add(mutableStation);

                                TTOM_PRINTER.printAttribute(mutableStation, 5);
                            }
                        }

                        MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes(mutableFeature, newStations);

                        LOGGER.info("*************************************************");
                        LOGGER.info("Original feature: ");
                        TTOM_PRINTER.printFeature(feature);
                        LOGGER.info("*************************************************");
                        LOGGER.info("Modified feature: ");
                        TTOM_PRINTER.printFeature(mutableFeature);
                        LOGGER.info("*************************************************");

                        // printStations(stations);

                        // update(line, mutableFeature);
                    } else {
                        errorsWriter.writeFallout(line + "||Could not find the feature");
                    }
                } catch (Exception exception) {
                    LOGGER.info("ERROR: ", exception);
                    errorsWriter.writeFallout(line + "||" + exception.getMessage());
                }
            }
            coreDB.disconnect();
            LOGGER.info("Done.");
            System.exit(0);

        } catch (CmdLineException exception) {
            LOGGER.error("Invalid options: " + Arrays.toString(args));
            LOGGER.error(exception.getLocalizedMessage());
            parser.printUsage(System.err);
        }
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
            System.out.println(stationId + "\t" + stationCounter + "\t" + spotCounter + "\t" + spotKey + "\t" + spot.getType());

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

    private static void update(String line, Feature<? extends Geometry> mutableFeature) throws IOException {
        if (mutableFeature != null) {
            // updateFeature(coreDB, mutableFeature, line);

            // System.out.println("===============================================");
            // TTOM_PRINTER.printFeature(feature);
            // System.out.println("===============================================");
            // TTOM_PRINTER.printFeature(mutableFeature);
            // System.out.println("===============================================");

        } else {
            errorsWriter.writeFallout(line + "||Evs station data in feature is ok");
        }
    }

    private static void printStations(Collection<Attribute<?>> stations) {
        int stationCounter = 0;
        for (Attribute<?> stationAttr : stations) {
            stationCounter++;
            NonSpatialObject station = (NonSpatialObject)stationAttr.getValue();
            Collection<Attribute<?>> spotAttrs = station.getAttributes(COMPOSITE_CHARGING_SPOT);

            int spotCounter = 0;

            for (Attribute<?> spotAttr : spotAttrs) {
                spotCounter++;
                NonSpatialObject spot = (NonSpatialObject)spotAttr.getValue();
                System.out.println(stationCounter + "\t" + spotCounter + "\t" + spot.getType());

                Collection<Attribute<?>> recpt = spot.getAttributes(E_V_CHARGING_RECEPTACLE_TYPE);
                for (Attribute<?> attribute : recpt) {
                    System.out.println("\t" + attribute.getValue());
                }

                Collection<Attribute<?>> faci = spot.getAttributes(E_V_CHARGING_FACILITIES);
                for (Attribute<?> attribute : faci) {
                    System.out.println("\t" + attribute.getValue());
                }

                Collection<Attribute<?>> srvs = spot.getAttributes(CHARGING_SPOT_SERVICES);
                for (Attribute<?> attribute : srvs) {
                    System.out.println("\t" + attribute.getValue());
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
                System.out.println(stationCounter + "\t" + spotCounter + "\t" + station + "\t" + spot);
            }
        }
        System.out.println("------------------");
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
                System.out.println(stationCounter + "\t" + spotCounter + "\t" + station + "\t" + spot);
            }
        }
        System.out.println("------------------");
    }

    private static void updateFeature(CoreDBClient coreDB, Feature<? extends Geometry> mutableFeature, String line) throws IOException {

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

                @Override
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

    private static String getQAResponse(CheckResults checkResult) {
        // System.out.println(checkResult.getQaResponse());
        QAResponse qaResponse = parseQaResponse(checkResult.getQaResponse());
        Set<Violation> violations = qaResponse.getViolations();
        List<String> violationMessages = Lists.newArrayList(Iterables.transform(violations, new Function<Violation, String>() {

            @Override
            public String apply(Violation input) {
                return input.getMessage();
            }
        }));
        return checkResult.getCheckStatus() + (violationMessages.size() > 0 ? " : " + violationMessages : "");
    }

    private static CoreDBClient createCoreDBClient() {
        CoreDBConnection coreDbConnection = new CoreDBConnection(CONFIG.coreDbUrl);
        // return new CoreDBClientImpl(coreDbConnection);
        AuthenticationServiceAccess authServiceAccess =
            new AuthenticationServiceAccess(true, CONFIG.authServerUrl, "atm", "atmsecret");
        AuthenticationManager authenticationManager = new AuthenticationManager(authServiceAccess);
        CommitterController committerController = new CommitterController(CONFIG.commitControllerUrl, "ArchiveToMDS");
        return new CoreDBClientWithCommitterController(coreDbConnection, CONFIG.accessPointUrl, CONFIG.baselineName,
            CONFIG.baselineVersion, committerController, authenticationManager, false, true, false);
    }

    private static Map<String, String> getMetadata() {
        Map<String, String> metadataParam = Maps.newHashMap();
        metadataParam.put("ToolName", "POI BrandName NC");
        metadataParam.put("ToolVersion", "1.0");
        metadataParam.put("UserID", CONFIG.userName);
        return metadataParam;
    }

    private static QAResponse parseQaResponse(String rawQaResponse) {
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
