package com.tomtom.places.misc.adminarea;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tomtom.places.misc.avro.AvroFileWriter;
import com.tomtom.places.unicorn.domain.avro.archive.RelatedArchivePlaceDiff;
import com.tomtom.places.unicorn.pipelineutil.AvroFileReader;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;

public class AAWiseDeltaSegregator {

    private final static Map<String, Integer> archiveDiffCount = Maps.newHashMap();
    private final static Map<String, Integer> relatedArchiveDiffCount = Maps.newHashMap();

    private final static Map<String, AvroFileWriter<RelatedArchivePlaceDiff>> writers = Maps.newHashMap();

    public static void main(String[] args) throws IOException {
        // segregateDelta("MEX", "E:/Places/documents/ArchiveToMDS/MEX/aa_geometry",
        // "E:/Places/documents/ArchiveToMDS/MEX/delta",
        // "E:/Places/documents/ArchiveToMDS/MEX/output");

        segregateDelta(args[0], args[1], args[2], args[3]);
    }

    private static void segregateDelta(String locality, String aaGeoPathPrefix, String deltaPath, String outputPath) throws IOException {
        AdminAreaFinder aaFinder = new AdminAreaFinder(new Locality(locality), aaGeoPathPrefix);
        HdfsTools hdfsTools = getHdfsTools();
        AvroFileReader<RelatedArchivePlaceDiff> reader = new AvroFileReader<RelatedArchivePlaceDiff>(deltaPath, hdfsTools);

        segregate(outputPath, aaFinder, reader);

        cleanup(reader);
        writeCountFile(outputPath);
    }

    private static void segregate(String outputPath, AdminAreaFinder aaFinder, AvroFileReader<RelatedArchivePlaceDiff> reader)
        throws IOException {
        int archivePlaceDiffCount = 0;
        int relatedCount = 0;
        for (RelatedArchivePlaceDiff diff : reader) {
            String aaId = aaFinder.getAdminAreaId(diff);
            AvroFileWriter<RelatedArchivePlaceDiff> writer = getAdminAreaWriter(outputPath, aaId);
            incrementCounts(aaId, diff);

            archivePlaceDiffCount += diff.getArchivePlaceDiffs().size();
            relatedCount++;
            if (relatedCount % 1000 == 0) {
                printCounts(archivePlaceDiffCount, relatedCount);
            }

            writer.write(diff);
            writer.flush();
        }
        printCounts(archivePlaceDiffCount, relatedCount);
    }

    private static void printCounts(int archivePlaceDiffCount, int relatedCount) {
        System.out.println("Processed [" + archivePlaceDiffCount + "] ArchivePlaceDiffs from [" + relatedCount
            + "] RelatedArchivePlaceDiffs.");
    }

    private static void cleanup(AvroFileReader<RelatedArchivePlaceDiff> reader) {
        for (AvroFileWriter<RelatedArchivePlaceDiff> w : writers.values()) {
            IOUtils.closeQuietly(w);
        }
        IOUtils.closeQuietly(reader);
    }

    private static void writeCountFile(String outputPath) throws IOException {
        List<String> result = Lists.newArrayList();

        Set<Entry<String, Integer>> entrySet = relatedArchiveDiffCount.entrySet();
        for (Entry<String, Integer> e : entrySet) {
            result.add(e.getKey() + "\t" + e.getValue() + "\t" + archiveDiffCount.get(e.getKey()));
        }

        FileUtils.writeLines(new File(outputPath + "/count.txt"), result);

    }

    private static void incrementCounts(String aaId, RelatedArchivePlaceDiff relatedDiff) throws IOException {
        incrementCount(aaId, relatedDiff.getArchivePlaceDiffs().size(), archiveDiffCount);
        incrementCount(aaId, 1, relatedArchiveDiffCount);
    }

    private static void incrementCount(String aaId, int toIncrement, Map<String, Integer> countMap) {
        Integer value = countMap.get(aaId);
        if (value == null) {
            value = 0;
        }
        countMap.put(aaId, value + toIncrement);
    }

    private static AvroFileWriter<RelatedArchivePlaceDiff> getAdminAreaWriter(String outputPath, String aaId) throws IOException {
        AvroFileWriter<RelatedArchivePlaceDiff> writer = writers.get(aaId);
        if (writer == null) {
            writer = new AvroFileWriter<RelatedArchivePlaceDiff>(outputPath + "/" + aaId + ".avro",
                RelatedArchivePlaceDiff.SCHEMA$, RelatedArchivePlaceDiff.class);
            writers.put(aaId, writer);
        }
        return writer;
    }

    private static HdfsTools getHdfsTools() {
        // System.setProperty("HADOOP_USER_NAME", "naiky");
        // Configuration config = HdfsTools.forName("hdfs://prod-hdp-nn-001.flatns.net/").getConfiguration();
        // config.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        // config.set("fs.file.impl", LocalFileSystem.class.getName());
        //
        // return HdfsTools.forConfiguration(config);
        return HdfsTools.forDefaultFileSystem();
    }
}
