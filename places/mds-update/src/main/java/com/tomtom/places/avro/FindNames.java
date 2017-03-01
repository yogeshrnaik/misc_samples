package com.tomtom.places.avro;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.teleatlas.global.common.ddct.DictionaryModelStoreFactory;
import com.teleatlas.models.ttom.TTOM;
import com.teleatlas.models.ttom.ttom_name.TTOM_Name;
import com.tomtom.cpu.api.features.Attribute;
import com.tomtom.cpu.api.features.Feature;
import com.tomtom.cpu.api.features.NonSpatialObject;
import com.tomtom.cpu.api.geometry.Geometry;
import com.tomtom.cpu.coredb.client.interfaces.Branch;
import com.tomtom.cpu.coredb.client.interfaces.Version;
import com.tomtom.places.unicorn.coredb.CoreDBClient;
import com.tomtom.places.unicorn.coredb.CoreDBClientImpl;
import com.tomtom.places.unicorn.coredb.CoreDBConnection;

public class FindNames {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindNames.class);

    private static final TTOM TTOM = new TTOM(DictionaryModelStoreFactory.getModelStore());
    private static final TTOM_Name TTOM_NAME = TTOM.TTOM_Name;
    private static final TtomPrinter TTOM_PRINTER = TtomPrinter.toSystemOut().withAssociations(false);

    private static CommandLineOptions CONFIG;

    public static void main(String[] args) throws Exception {
        CONFIG = new CommandLineOptions();
        CmdLineParser parser = new CmdLineParser(CONFIG);
        try {
            parser.parseArgument(args);
            LOGGER.info("Arguments: " + Arrays.toString(args));

            FalloutWriter outputWriter = new FalloutWriter(CONFIG.inputFile + ".output");
            FalloutWriter errorsWriter = new FalloutWriter(CONFIG.inputFile + ".errors");

            List<String> lines = FileUtils.readLines(new File(CONFIG.inputFile), Charset.forName("UTF-8"));

            CoreDBClient coreDB = createCoreDBClient();
            coreDB.connect();
            Branch branch = coreDB.getCurrentBranch();
            for (String line : lines) {
                String featureId = line;
                List<String> names = Lists.newArrayList();
                try {
                    Version version = coreDB.getCurrentVersion(branch);
                    Feature<? extends Geometry> feature =
                        coreDB.getFeatureById(featureId, version.getJournalVersion(), branch.getBranchId().toString());
                    if (feature != null && feature.getGeometry() != null) {
                        Collection<Attribute<?>> attributes = feature.getAttributes();
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
                                    names.add(nameText);
                                }
                            }
                        }
                    } else {
                        errorsWriter.writeFallout(line + "|Could not find the feature");
                    }

                    if (names.isEmpty()) {
                        System.out.println("===============================================");
                        TTOM_PRINTER.printFeature(feature);
                        System.out.println("===============================================");
                    }

                    outputWriter.writeFallout(line + "|" + Joiner.on("|").join(names));

                } catch (Exception exception) {
                    errorsWriter.writeFallout(line + "|" + exception.getMessage());
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
        return new CoreDBClientImpl(coreDbConnection, CONFIG.accessPointUrl, CONFIG.baselineName, CONFIG.baselineVersion, false, true,
            false);
    }
}
