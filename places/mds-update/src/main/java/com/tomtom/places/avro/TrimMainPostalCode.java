package com.tomtom.places.avro;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teleatlas.global.common.ddct.DictionaryModelStoreFactory;
import com.teleatlas.models.ttom.TTOM;
import com.tomtom.common.serdeser.Deserializer;
import com.tomtom.common.serdeser.json.JsonDeserializer;
import com.tomtom.cpu.api.features.Attribute;
import com.tomtom.cpu.api.features.Feature;
import com.tomtom.cpu.api.features.NonSpatialObject;
import com.tomtom.cpu.api.geometry.Geometry;
import com.tomtom.cpu.coredb.client.interfaces.Branch;
import com.tomtom.cpu.coredb.client.interfaces.Version;
import com.tomtom.cpu.coredb.mutable.MutableAttribute;
import com.tomtom.cpu.coredb.mutable.MutableObjectManipulator;
import com.tomtom.cpu.coredb.quality.CheckResults;
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

public class TrimMainPostalCode {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrimMainPostalCode.class);

    private static final TTOM TTOM = new TTOM(DictionaryModelStoreFactory.getModelStore());
    private static final MutableObjectManipulator MUTABLE_FEATURE_MANIPULATOR = new MutableObjectManipulator();
    private static final TtomPrinter TTOM_PRINTER = TtomPrinter.toSystemOut().withAssociations(false);

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

            List<String> featureIDs = FileUtils.readLines(new File(CONFIG.inputFile), Charset.forName("UTF-8"));

            CoreDBClient coreDB = createCoreDBClient();
            coreDB.connect();
            Branch branch = coreDB.getCurrentBranch();
            for (String featureId : featureIDs) {
                try {
                    Version version = coreDB.getCurrentVersion(branch);
                    Feature<? extends Geometry> feature =
                        coreDB.getFeatureById(featureId, version.getJournalVersion(), branch.getBranchId().toString());
                    if (feature != null && feature.getGeometry() != null) {
                        Feature<? extends Geometry> mutableFeature = MUTABLE_FEATURE_MANIPULATOR.createMutableFeature(feature);
                        Collection<Attribute<?>> addresses =
                            mutableFeature.getAttributes(feature.getType().getProperty("AddressInformation"));

                        NonSpatialObject addressInformation = null;
                        boolean update = false;
                        for (Attribute<?> address : addresses) {
                            addressInformation = (NonSpatialObject)address.getValue();
                            Collection<Attribute<?>> mainPostalCode =
                                addressInformation.getAttributes(TTOM.TTOM_POI.FEATURES.AddressInformation.MainPostalCode);
                            if (mainPostalCode.size() > 0) {
                                Attribute<?> mainPostalCodeAttr = mainPostalCode.iterator().next();
                                String value = (String)mainPostalCodeAttr.getValue();
                                String trimmedValue = value.trim();
                                if (!value.equals(trimmedValue)) {
                                    System.out.println("Feature ID: " + featureId + ", MainPostalCode ==> Old: \"" + value + "\", New: \""
                                        + trimmedValue + "\"");

                                    @SuppressWarnings("unchecked")
                                    MutableAttribute<String> newMainPostalCodeAttr =
                                        MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(mainPostalCodeAttr);
                                    newMainPostalCodeAttr.setValue(trimmedValue);
                                    Collection<Attribute<?>> newSubAttributes = Lists.newArrayList();
                                    newSubAttributes.add(newMainPostalCodeAttr);
                                    MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes(addressInformation, newSubAttributes);

                                    update = true;
                                }
                            }
                        }

                        if (update) {
                            // System.out.println("===============================================");
                            // TTOM_PRINTER.printFeature(feature);
                            // System.out.println("===============================================");
                            // TTOM_PRINTER.printFeature(mutableFeature);
                            // System.out.println("===============================================");

                            updateFeature(coreDB, mutableFeature, featureId);

                        } else {
                            errorsWriter.writeFallout(featureId + "||MainPostalCode does not end with a space");
                        }
                    } else {
                        errorsWriter.writeFallout(featureId + "||Could not find the feature");
                    }
                } catch (Exception exception) {
                    errorsWriter.writeFallout(featureId + "||" + exception.getMessage());
                }
            }
            // coreDB.disconnect();
            LOGGER.info("Done.");
            System.exit(0);

        } catch (CmdLineException exception) {
            LOGGER.error("Invalid options: " + Arrays.toString(args));
            LOGGER.error(exception.getLocalizedMessage());
            parser.printUsage(System.err);
        }
    }

    private static void updateFeature(CoreDBClient coreDB, Feature<? extends Geometry> mutableFeature, String featureId)
        throws IOException {

        String txnId = "";
        try {
            coreDB.beginTransaction();
            txnId = coreDB.getCurrentTransactionId();
            coreDB.setTransactionMetaData(null, getMetadata());
            coreDB.updateFeature(mutableFeature);

            CheckResults checkResult = coreDB.checkTransaction();
            LOGGER.info("QA Response: " + getQAResponse(checkResult));
            // coreDB.rollbackTransaction();

            // coreDB.commitTransactionWithoutRollback();

            updatedWriter.writeFallout(featureId + "|" + txnId);
            LOGGER.info("Updated feature: " + featureId + ", Transaction: " + txnId);

            // } catch (CheckException exception) {
            // QAResponse qaResponse = parseQaResponse(exception.getDetails());
            // Set<Violation> violations = qaResponse.getViolations();
            // List<String> violationMessages = Lists.newArrayList(Iterables.transform(violations, new Function<Violation, String>() {
            //
            // @Override
            // public String apply(Violation input) {
            // return input.getMessage();
            // }
            // }));
            // errorsWriter.writeFallout(featureId + "|" + txnId + "|Violations: " + violationMessages.toString());
        } catch (Exception exception) {
            retryWriter.writeFallout(featureId + "|" + txnId + "|" + exception.getMessage());
            try {
                if (txnId != null) {
                    // coreDB.rollbackTransaction();
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
