package com.tomtom.places.archive.checker;

import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.PGroupedTable;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.PipelineResult;
import com.cloudera.crunch.PipelineResult.StageResult;
import com.cloudera.crunch.impl.mem.MemPipeline;
import com.cloudera.crunch.impl.mr.MRPipeline;
import com.cloudera.crunch.io.avro.AvroFileSource;
import com.cloudera.crunch.types.avro.Avros;
import com.cloudera.crunch.types.writable.Writables;
import com.google.common.collect.Lists;
import com.tomtom.places.archive.checker.checks.ApplyArchiveChecks;
import com.tomtom.places.archive.checker.checks.ArchiveChecksFactory;
import com.tomtom.places.archive.checker.checks.CheckKeyFn;
import com.tomtom.places.archive.checker.report.CheckReport;
import com.tomtom.places.archive.checker.report.CheckReportDoFn;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.archive.checker.result.OccurrenceCheckResult;
import com.tomtom.places.archive.checker.util.ArchivePlaceCounter;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class ArchiveChecker extends Configured implements Tool {

    private final String archivePlacesPath;
    private final String archiveCheckerReportsPath;

    public ArchiveChecker(String inputPath, String outputPath) {
        archivePlacesPath = inputPath;
        archiveCheckerReportsPath = outputPath;
    }

    public int run(String[] args) throws Exception {
        Pipeline pipeline = new MRPipeline(ArchiveCheckerTest.class, getConf());
        return run(pipeline);
    }

    public int run(Pipeline pipeline) {
        PCollection<ArchivePlace> archivePlaces = pipeline.read(
            new AvroFileSource<ArchivePlace>(new Path(archivePlacesPath), Avros.records(ArchivePlace.class)));

        PCollection<CheckResult> results =
            archivePlaces.parallelDo(new ApplyArchiveChecks(), Writables.records(CheckResult.class));

        PCollection<CheckResult> nullCheckResults =
            MemPipeline.collectionOf(Lists.<CheckResult>newArrayList(new OccurrenceCheckResult(0, ArchiveChecksFactory.getCheck("CI_1.47"),
                null)));

        results = nullCheckResults.union(results);

        PGroupedTable<String, CheckResult> groupByKey = results.by(new CheckKeyFn(), Writables.strings()).groupByKey();

        // PCollection<String> checksHavingResult = groupByKey.parallelDo(new CheckResultsWriter(), Avros.strings());
        // List<String> checks = Lists.newArrayList(checksHavingResult.materialize());

        PCollection<CheckReport> pReports = groupByKey.parallelDo(new CheckReportDoFn(), Writables.records(CheckReport.class));
        // pipeline.writeTextFile(pReports, archiveCheckerReportsPath + File.separator + "reports.txt");

        List<CheckReport> reports = Lists.newArrayList(pReports.materialize());
        for (CheckReport report : reports) {
            System.out.println(report);
        }

        PipelineResult result = pipeline.done();
        printCounters(result);
        return result.succeeded() ? 0 : 1;
    }

    private static void printCounters(PipelineResult result) {
        for (StageResult stageResult : result.getStageResults()) {
            for (ArchivePlaceCounter counterName : ArchivePlaceCounter.values()) {
                Counter counter = stageResult.findCounter(counterName);
                long value = counter.getValue();
                System.out.println(counterName + " : " + value);
            }
        }
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
