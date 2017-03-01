package com.tomtom.places.avro;

import java.io.File;
import java.io.IOException;
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
import com.teleatlas.global.common.ddct.DictionaryRangeItem;
import com.teleatlas.models.ttom.TTOM;
import com.teleatlas.models.ttom.ttom_name.TTOM_Name;
import com.tomtom.common.serdeser.Deserializer;
import com.tomtom.common.serdeser.json.JsonDeserializer;
import com.tomtom.cpu.api.features.Attribute;
import com.tomtom.cpu.api.features.AttributedObject;
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
import com.tomtom.places.unicorn.coredb.CommitterController;
import com.tomtom.places.unicorn.coredb.CoreDBClient;
import com.tomtom.places.unicorn.coredb.CoreDBClientWithCommitterController;
import com.tomtom.places.unicorn.coredb.CoreDBConnection;
import com.tomtom.qa.qa_io.response.QAResponse;
import com.tomtom.qa.qa_io.response.QAResponseImpl;
import com.tomtom.qa.qa_io.violation.Violation;

import cern.colt.Arrays;

public class UpdateBrandLanguageCode {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBrandLanguageCode.class);

    private static final TTOM TTOM = new TTOM(DictionaryModelStoreFactory.getModelStore());
    private static final TTOM_Name TTOM_NAME = TTOM.TTOM_Name;
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

            List<String> lines = FileUtils.readLines(new File(CONFIG.inputFile), Charset.forName("UTF-8"));

            CoreDBClient coreDB = createCoreDBClient();
            coreDB.connect();
            Branch branch = coreDB.getCurrentBranch();
            for (String line : lines) {
                try {
                    Iterator<String> parts = Splitter.on("|").split(line).iterator();
                    String featureId = parts.next();
                    String brandNameText = parts.next();

                    Version version = coreDB.getCurrentVersion(branch);

                    // Parameters parameters = ParametersBuilder.newBuilder().withVersion(version).withBranch(branch)
                    // .withNoAssociations().withAttributes(TTOM.TTOM_POI.FEATURES.Service.BrandName).buildWIthoutSpatialFilter();
                    // Feature<? extends Geometry> feature = coreDB.getFeatureById(UUID.fromString(featureId), parameters);
                    Feature<? extends Geometry> feature =
                        coreDB.getFeatureById(featureId, version.getJournalVersion(), branch.getBranchId().toString());
                    if (feature != null && feature.getGeometry() != null) {
                        Feature<? extends Geometry> mutableFeature = MUTABLE_FEATURE_MANIPULATOR.createMutableFeature(feature);
                        Collection<Attribute<?>> brandNames = mutableFeature.getAttributes(feature.getType().getProperty("BrandName"));
                        // Collection<Attribute<?>> brandNames = mutableFeature.getAttributes(TTOM.TTOM_POI.FEATURES.Service.BrandName);
                        // System.out.println(brandNames);

                        boolean update = false;
                        for (Attribute<?> brandName : brandNames) {
                            NonSpatialObject nameSet = (NonSpatialObject)brandName.getValue();
                            Collection<Attribute<?>> transliterationSetAttrs =
                                nameSet.getAttributes(TTOM_NAME.FEATURES.NameSet.NameTransliterationSet);
                            for (Attribute<?> transliterationSetAttr : transliterationSetAttrs) {
                                NonSpatialObject transliterationSet = (NonSpatialObject)transliterationSetAttr.getValue();

                                if (match(transliterationSet, brandNameText)) {
                                    Attribute<?> languageCodeAttr =
                                        transliterationSet.getAttributes(TTOM_NAME.FEATURES.NameSet.NameTransliterationSet.LanguageCode)
                                            .iterator()
                                            .next();
                                    Attribute<?> isoLanguageCodeAttr = transliterationSet
                                        .getAttributes(TTOM_NAME.FEATURES.NameSet.NameTransliterationSet.ISOLanguageCode).iterator()
                                        .next();

                                    @SuppressWarnings("unchecked")
                                    MutableAttribute<DictionaryRangeItem> newLanguageCodeAttr =
                                        MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(languageCodeAttr);
                                    newLanguageCodeAttr.setValue(TTOM_NAME.RANGES.LanguageCode.Undetermined);
                                    @SuppressWarnings("unchecked")
                                    MutableAttribute<String> newISOLanguageCodeAttr =
                                        MUTABLE_FEATURE_MANIPULATOR.createMutableAttribute(isoLanguageCodeAttr);
                                    newISOLanguageCodeAttr.setValue("UND");

                                    Collection<Attribute<?>> newSubAttributes = Lists.newArrayList();
                                    newSubAttributes.add(newLanguageCodeAttr);
                                    newSubAttributes.add(newISOLanguageCodeAttr);
                                    MUTABLE_FEATURE_MANIPULATOR.replaceSelectedAttributes(transliterationSet, newSubAttributes);

                                    update = true;
                                    break;
                                }
                            }
                            if (update) {
                                break;
                            }
                        }

                        if (update) {
                            updateFeature(coreDB, mutableFeature, line);

                            // System.out.println("===============================================");
                            // TTOM_PRINTER.printFeature(feature);
                            // System.out.println("===============================================");
                            // TTOM_PRINTER.printFeature(mutableFeature);
                            // System.out.println("===============================================");

                        } else {
                            errorsWriter.writeFallout(line + "||Old value for brand name not found for the feature");
                        }
                    } else {
                        errorsWriter.writeFallout(line + "||Could not find the feature");
                    }
                } catch (Exception exception) {
                    errorsWriter.writeFallout(line + "||" + exception.getMessage());
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

    private static boolean match(AttributedObject transliterationSet, String brandNameText) {
        Attribute<?> nameAttr = transliterationSet
            .getAttributes(TTOM_NAME.FEATURES.NameSet.NameTransliterationSet.Name)
            .iterator().next();
        NonSpatialObject name = (NonSpatialObject)nameAttr.getValue();

        Attribute<?> nameTextAttr =
            name.getAttributes(TTOM_NAME.FEATURES.NameSet.NameTransliterationSet.Name.NameText)
                .iterator().next();
        String nameText = (String)nameTextAttr.getValue();
        return nameText.equals(brandNameText);
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
