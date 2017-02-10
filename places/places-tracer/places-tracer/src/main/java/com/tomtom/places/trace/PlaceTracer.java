package com.tomtom.places.trace;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.PGroupedTable;
import com.cloudera.crunch.Pair;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.PipelineResult;
import com.cloudera.crunch.Target;
import com.cloudera.crunch.impl.mr.MRPipeline;
import com.cloudera.crunch.io.avro.AvroFileTarget;
import com.cloudera.crunch.types.avro.Avros;
import com.tomtom.places.unicorn.domain.avro.tracer.PlaceTrace;
import com.tomtom.places.unicorn.pipeline.repository.RunDescriptorSupport;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;
import com.tomtom.places.unicorn.rundescriptor.ArtifactId;

public class PlaceTracer extends Configured implements Tool {

    private final String runDescriptorPath;
    private final RunDescriptorSupport rds;
    private final ArtifactReader reader;

    public PlaceTracer(String runDescriptorPath) throws Exception {
        this(runDescriptorPath, HdfsTools.forDefaultFileSystem().getConfiguration());
    }

    public PlaceTracer(String runDescriptorPath, Configuration conf) throws Exception {
        super(conf);
        this.runDescriptorPath = runDescriptorPath;
        rds = RunDescriptorSupport.loadFromFile(conf, runDescriptorPath);
        reader = new ArtifactReader(runDescriptorPath, rds, HdfsTools.forDefaultFileSystem());
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
        ArtifactTracer wrapper = new ArtifactTracer(locality, rds, HdfsTools.forDefaultFileSystem());
        PCollection<Pair<String, PlaceTrace>> artifacts = wrapper.getAllArtifacts(locality, pipeline);
        PGroupedTable<String, Pair<String, PlaceTrace>> groupByKey = artifacts.by(new PlaceKeyFn(), Avros.strings()).groupByKey();
        PCollection<PlaceTrace> traced = groupByKey.parallelDo(new PlaceTracerDoFn(), Avros.records(PlaceTrace.class));

        // TODO: gather traces logged with ClusterPlaceID and/or ArchivePlaceID
        // 1) emit traced collection as Pair<ClusteredPlaceId, PlaceTrackInfo>
        // 2) read and emit archive places as Pair<ArchivePlaceId, Wrap ArchivePlace into PlackTrackInfo>
        // 3) how to emit and combine traces that were logged with ClusteredPlaceId?
        // union 1, 2 & 3 and groupByKey() then in process wrap all places together into PlaceTrackInfo and emit wrapped PlaceTrackInfo
        writeTraced(pipeline, traced);
    }

    private void writeTraced(Pipeline pipeline, PCollection<PlaceTrace> traced) throws IOException {
        String placeTracesPath = runDescriptorPath.substring(0, runDescriptorPath.indexOf("run-descriptor") - 1) + "/place-traces";
        HdfsTools.forDefaultFileSystem().mkdirs(placeTracesPath);
        Target target = new AvroFileTarget(placeTracesPath);
        pipeline.write(traced, target);
    }
}
