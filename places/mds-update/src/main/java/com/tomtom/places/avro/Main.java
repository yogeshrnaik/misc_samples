package com.tomtom.places.avro;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.teleatlas.global.common.ddct.DictionaryModelStoreFactory;
import com.teleatlas.models.ttom.TTOM;
import com.teleatlas.models.ttom.ttom_name.TTOM_Name;
import com.tomtom.common.serdeser.Deserializer;
import com.tomtom.common.serdeser.json.JsonDeserializer;
import com.tomtom.cpu.api.features.Attribute;
import com.tomtom.cpu.api.features.Feature;
import com.tomtom.cpu.api.features.NonSpatialObject;
import com.tomtom.cpu.api.geometry.Geometry;
import com.tomtom.cpu.coredb.client.interfaces.Branch;
import com.tomtom.cpu.coredb.client.interfaces.CheckException;
import com.tomtom.cpu.coredb.client.interfaces.Version;
import com.tomtom.cpu.coredb.common.dto.MetaDataObjectType;
import com.tomtom.cpu.coredb.common.dto.MetadataRequest;
import com.tomtom.cpu.coredb.mutable.MutableAttribute;
import com.tomtom.cpu.coredb.mutable.MutableObjectManipulator;
import com.tomtom.mau.authtoken.AuthenticationManager;
import com.tomtom.mau.authtoken.AuthenticationServiceAccess;
import com.tomtom.places.unicorn.coredb.CommitterController;
import com.tomtom.places.unicorn.coredb.CoreDBClient;
import com.tomtom.places.unicorn.coredb.CoreDBClientWithCommitterController;
import com.tomtom.places.unicorn.coredb.CoreDBConnection;
import com.tomtom.qa.qa_io.response.QAResponse;
import com.tomtom.qa.qa_io.response.QAResponseImpl;
import com.tomtom.qa.qa_io.violation.Violation;

import cern.colt.Arrays;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final TTOM TTOM = new TTOM(DictionaryModelStoreFactory.getModelStore());
    private static final TTOM_Name TTOM_NAME = TTOM.TTOM_Name;
    private static final MutableObjectManipulator MUTABLE_FEATURE_MANIPULATOR = new MutableObjectManipulator();
    // private static final TtomPrinter TTOM_PRINTER = TtomPrinter.toSystemOut().withAssociations(false);

    private static CommandLineOptions CONFIG;

    public static void main(String[] args) throws Exception {
        CONFIG = new CommandLineOptions();
        CmdLineParser parser = new CmdLineParser(CONFIG);
        try {
            parser.parseArgument(args);
            LOGGER.info("Arguments: " + Arrays.toString(args));

            FalloutWriter updatedWriter = new FalloutWriter(CONFIG.inputFile + ".updated");
            FalloutWriter errorsWriter = new FalloutWriter(CONFIG.inputFile + ".errors");
            FalloutWriter retryWriter = new FalloutWriter(CONFIG.inputFile + ".retry");

            List<String> lines = FileUtils.readLines(new File(CONFIG.inputFile), Charset.forName("UTF-8"));

            CoreDBClient coreDB = createCoreDBClient();
            coreDB.connect();
            Branch branch = coreDB.getCurrentBranch();
            for (String line : lines) {
                Iterator<String> parts = Splitter.on("|").split(line).iterator();
                String featureId = parts.next();
                String oldValue = parts.next();
                String newValue = parts.next();
                String txnId = null;
                try {
                    Version version = coreDB.getCurrentVersion(branch);
                    Feature<? extends Geometry> feature =
                        coreDB.getFeatureById(featureId, version.getJournalVersion(), branch.getBranchId().toString());
                    if (feature != null && feature.getGeometry() != null) {
                        Feature<? extends Geometry> mutableFeature = MUTABLE_FEATURE_MANIPULATOR.createMutableFeature(feature);
                        Collection<Attribute<?>> attributes = mutableFeature.getAttributes();
                        boolean replaced = false;
                        for (Attribute<?> attribute : attributes) {
                            if (attribute.getType().getTypeShortName().equals("CommonNameSet")) {
                                NonSpatialObject commonNameSet = (NonSpatialObject)attribute.getValue();
                                NonSpatialObject nameSet = (NonSpatialObject)commonNameSet
                                    .getAttributes(TTOM_NAME.FEATURES.CommonNameSet.NameSet).iterator().next().getValue();
                                Collection<Attribute<?>> transliterationSets =
                                    nameSet.getAttributes(TTOM_NAME.FEATURES.CommonNameSet.NameSet.NameTransliterationSet);
                                for (Attribute<?> transliterationSetAttr : transliterationSets) {
                                    NonSpatialObject transliterationSet = (NonSpatialObject)transliterationSetAttr.getValue();

                                    Attribute<?> nameAttr = transliterationSet
                                        .getAttributes(TTOM_NAME.FEATURES.CommonNameSet.NameSet.NameTransliterationSet.Name)
                                        .iterator().next();
                                    NonSpatialObject name = (NonSpatialObject)nameAttr.getValue();

                                    Attribute<?> nameTextAttr =
                                        name.getAttributes(TTOM_NAME.FEATURES.CommonNameSet.NameSet.NameTransliterationSet.Name.NameText)
                                            .iterator().next();
                                    String nameText = (String)nameTextAttr.getValue();

                                    if (nameText.equals(oldValue)) {
                                        @SuppressWarnings("unchecked")
                                        MutableAttribute<String> newAttribute =
                                            MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(nameTextAttr);
                                        newAttribute.setValue(newValue);
                                        Collection<Attribute<?>> newAttributes = Lists.newArrayList();
                                        newAttributes.add(newAttribute);
                                        MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes(name, newAttributes);

                                        coreDB.beginTransaction();
                                        txnId = coreDB.getCurrentTransactionId();
                                        coreDB.setTransactionMetaData(null, getMetadata());
                                        coreDB.updateFeature(mutableFeature);
                                        // CheckResults checkResult = coreDB.checkTransaction();
                                        coreDB.commitTransactionWithoutRollback();
                                        // coreDB.rollbackTransaction();

                                        updatedWriter.writeFallout(line + "|" + txnId);
                                        LOGGER.info("Updated feature: " + featureId + ", Old Name: " + nameText + ", New Name: " + name
                                            .getAttributes(TTOM_NAME.FEATURES.CommonNameSet.NameSet.NameTransliterationSet.Name.NameText)
                                            .iterator().next().getValue() + ", Transaction: " + txnId);
                                        // System.out.println("===============================================");
                                        // TTOM_PRINTER.printFeature(feature);
                                        // System.out.println("===============================================");
                                        // TTOM_PRINTER.printFeature(mutableFeature);
                                        // System.out.println("===============================================");
                                        replaced = true;
                                        break;
                                    }
                                }
                                if (replaced) {
                                    break;
                                }
                            }
                        }
                        if (!replaced) {
                            errorsWriter.writeFallout(line + "||Old value for name not found for the feature");
                        }
                    } else {
                        errorsWriter.writeFallout(line + "||Could not find the feature");
                    }
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
                            // write.saveTransaction(transaction);
                            // write.rollback(transaction);
                        }
                    } catch (Exception ex) {
                    }
                }
            }

            LOGGER.info("Done.");
            System.exit(0);

        } catch (CmdLineException exception) {
            LOGGER.error("Invalid options: " + Arrays.toString(args));
            LOGGER.error(exception.getLocalizedMessage());
            parser.printUsage(System.err);
        }
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

    private static Map<MetadataRequest, Collection<String>> getMetadata(String txnId) {
        Map<MetadataRequest, Collection<String>> metadataParam = Maps.newHashMap();
        metadataParam.put(new MetadataRequest(txnId, null, MetaDataObjectType.TRANSACTION, "ToolName", null),
            Lists.newArrayList("POI Nameset NC"));
        metadataParam.put(new MetadataRequest(txnId, null, MetaDataObjectType.TRANSACTION, "ToolVersion", null),
            Lists.newArrayList("1.0"));
        metadataParam.put(new MetadataRequest(txnId, null, MetaDataObjectType.TRANSACTION, "UserID", null),
            Lists.newArrayList(CONFIG.userName));
        return metadataParam;
    }

    private static Map<String, String> getMetadata() {
        Map<String, String> metadataParam = Maps.newHashMap();
        metadataParam.put("ToolName", "POI Nameset NC");
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
