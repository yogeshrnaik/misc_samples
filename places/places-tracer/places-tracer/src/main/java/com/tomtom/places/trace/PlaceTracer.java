package com.tomtom.places.trace;

import java.io.IOException;
import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.PGroupedTable;
import com.cloudera.crunch.Pair;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.PipelineResult;
import com.cloudera.crunch.Target;
import com.cloudera.crunch.impl.mr.MRPipeline;
import com.cloudera.crunch.io.avro.AvroFileSource;
import com.cloudera.crunch.io.avro.AvroFileTarget;
import com.cloudera.crunch.types.avro.Avros;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.clustered.ClusteredPlace;
import com.tomtom.places.unicorn.domain.avro.normalized.NormalizedPlace;
import com.tomtom.places.unicorn.domain.avro.tracer.PlaceTrace;
import com.tomtom.places.unicorn.pipeline.repository.RunDescriptorSupport;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;
import com.tomtom.places.unicorn.rundescriptor.ArtifactId;

public class PlaceTracer extends Configured implements Tool {

    private final String runDescriptorPath;
    private final RunDescriptorSupport rds;

    public PlaceTracer(String runDescriptorPath) throws Exception {
        this(runDescriptorPath, HdfsTools.forDefaultFileSystem().getConfiguration());
    }

    public PlaceTracer(String runDescriptorPath, Configuration conf) throws Exception {
        super(conf);
        this.runDescriptorPath = runDescriptorPath;
        rds = RunDescriptorSupport.loadFromFile(conf, runDescriptorPath);
    }

    public static void main(String[] args) throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        ToolRunner.run(new Configuration(), new PlaceTracer(args[0]), args);
        System.out.println("Finished in: " + DurationFormatUtils.formatDuration(watch.getTime(), "HH 'hr', mm 'min', ss 'sec'"));
    }

    public int run(String[] args) throws Exception {
        Pipeline pipeline = new MRPipeline(PlaceTracer.class, getConf());
        PipelineResult result = run(pipeline);
        return result.succeeded() ? 0 : -1;
    }

    public PipelineResult run(Pipeline pipeline) throws Exception {
        String mappedPlaces = rds.getOutputArtifactPath(ArtifactId.MAPPED_PLACES, true);
        List<String> dirs = HdfsTools.forDefaultFileSystem().listDirectories(mappedPlaces, ".*");
        for (String locality : dirs) {
            createPlaceTraces(locality, pipeline);
        }
        return pipeline.done();
    }

    @SuppressWarnings("unchecked")
    public void createPlaceTraces(String locality, Pipeline pipeline) throws Exception {

        PCollection<Pair<String, PlaceTrace>> mapped = wrapArtifact(ArtifactId.MAPPED_PLACES, NormalizedPlace.class, pipeline);
        PCollection<Pair<String, PlaceTrace>> clustered = wrapArtifact(ArtifactId.CLUSTERED_PLACES, ClusteredPlace.class, pipeline);
        PCollection<Pair<String, PlaceTrace>> archives = wrapArtifact(ArtifactId.ARCHIVE_PLACES, ArchivePlace.class, pipeline);
        // PCollection<Pair<String, PlaceTrace>> traces = wrapArtifact(ArtifactId.TRACE_DUMP, Trace.class, pipeline);

        PCollection<Pair<String, PlaceTrace>> union = mapped.union(clustered).union(archives)/* .union(traces) */;

        PGroupedTable<String, Pair<String, PlaceTrace>> groupByKey = union.by(new PlaceKeyFn(), Avros.strings()).groupByKey();

        PCollection<PlaceTrace> traced = groupByKey.parallelDo(new PlaceTracerDoFn(), Avros.records(PlaceTrace.class));

        // TODO: gather traces logged with ClusterPlaceID and/or ArchivePlaceID
        // 1) emit traced collection as Pair<ClusteredPlaceId, PlaceTrackInfo>
        // 2) read and emit archive places as Pair<ArchivePlaceId, Wrap ArchivePlace into PlackTrackInfo>
        // 3) how to emit and combine traces that were logged with ClusteredPlaceId?
        // union 1, 2 & 3 and groupByKey() then in process wrap all places together into PlaceTrackInfo and emit wrapped PlaceTrackInfo
        writeTraced(pipeline, traced);
    }

    private <T extends SpecificRecordBase> PCollection<Pair<String, PlaceTrace>>
        wrapArtifact(ArtifactId artifactId, Class<T> clazz, Pipeline pipeline) throws Exception {
        PCollection<T> artifact = readArtifact(artifactId, clazz, pipeline);
        return artifact.parallelDo(artifactId.toString(), new KeyDoFn(), Avros.pairs(Avros.strings(), Avros.records(clazz)));
    }

    private void writeTraced(Pipeline pipeline, PCollection<PlaceTrace> traced) throws IOException {
        String placeTracesPath = runDescriptorPath.substring(0, runDescriptorPath.indexOf("run-descriptor") - 1) + "/place-traces";
        HdfsTools.forDefaultFileSystem().mkdirs(placeTracesPath);
        Target target = new AvroFileTarget(placeTracesPath);
        pipeline.write(traced, target);
    }

    private <T extends SpecificRecordBase> PCollection<T> readArtifact(ArtifactId artifact, Class<T> clazz,
        Pipeline pipeline) throws Exception {
        String artifactPath = rds.getOutputArtifactPath(artifact, true);
        return readRecursively(clazz, artifactPath, pipeline);
    }

    public static <T extends SpecificRecordBase> PCollection<T> readRecursively(Class<T> clazz, String parentPath, Pipeline pipeline)
        throws Exception {
        if (HdfsTools.forDefaultFileSystem().isFile(parentPath)) {
            return readFile(clazz, parentPath, pipeline);
        }

        List<String> dirs = HdfsTools.forDefaultFileSystem().listAll(parentPath, ".*");
        PCollection<T> result = null;
        for (String dir : dirs) {
            PCollection<T> data = readRecursively(clazz, parentPath + "/" + dir, pipeline);
            result = result == null ? data : result.union(data);
        }
        return result;
    }

    private static <T extends SpecificRecordBase> PCollection<T> readFile(Class<T> clazz, String parentPath, Pipeline pipeline) {
        AvroFileSource<T> source = new AvroFileSource<T>(new Path(parentPath), Avros.records(clazz));
        return pipeline.read(source);
    }

}
