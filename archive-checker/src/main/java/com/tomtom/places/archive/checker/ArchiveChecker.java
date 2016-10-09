package com.tomtom.places.archive.checker;

import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.PipelineResult;
import com.cloudera.crunch.impl.mr.MRPipeline;
import com.cloudera.crunch.io.avro.AvroFileSource;
import com.cloudera.crunch.types.avro.Avros;
import com.cloudera.crunch.types.writable.Writables;
import com.tomtom.places.archive.checker.checks.ApplyArchiveChecks;
import com.tomtom.places.archive.checker.checks.CheckKeyFn;
import com.tomtom.places.archive.checker.report.CheckReport;
import com.tomtom.places.archive.checker.report.ValidationResultsConsolidator;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class ArchiveChecker extends Configured implements Tool {

    private final String archivePlacesPath;
    private final String archiveCheckerReportsPath;

    public ArchiveChecker(String inputPath, String outputPath) {
        archivePlacesPath = inputPath;
        archiveCheckerReportsPath = outputPath;
    }

    public int run(String[] args) throws Exception {
        Pipeline pipeline = new MRPipeline(ArchiveChecker.class, getConf());

        PCollection<ArchivePlace> archivePlaces = pipeline.read(
            new AvroFileSource<ArchivePlace>(new Path(archivePlacesPath), Avros.records(ArchivePlace.class)));

        PCollection<CheckResult> results =
            archivePlaces.parallelDo(new ApplyArchiveChecks(), Writables.records(CheckResult.class));
        PCollection<CheckReport> checkReports = results.by(new CheckKeyFn(), Avros.strings()).groupByKey()
            .parallelDo(new ValidationResultsConsolidator(), Writables.records(CheckReport.class));

        PipelineResult result = pipeline.done();
        return result.succeeded() ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Invalid arguments: " + Arrays.toString(args));
            System.err.println("Usage: hadoop jar archive-checker-X.y-SNAPSHOT-job.jar "
                + "<input-archive-places-path> <archive-checker-reports-path>");
            System.err.println();
            System.exit(1);
        }

        ToolRunner.run(new Configuration(), new ArchiveChecker(args[0], args[1]), args);
    }
}
