package com.tomtom.places.trace;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cloudera.crunch.MapFn;
import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.PGroupedTable;
import com.cloudera.crunch.Pair;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.PipelineResult;
import com.cloudera.crunch.impl.mr.MRPipeline;
import com.cloudera.crunch.io.avro.AvroFileSource;
import com.cloudera.crunch.types.avro.Avros;
import com.tomtom.places.trace.util.Utils;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.clustered.ClusteredPlace;
import com.tomtom.places.unicorn.domain.avro.normalized.NormalizedPlace;
import com.tomtom.places.unicorn.domain.avro.trace.Trace;
import com.tomtom.places.unicorn.domain.avro.tracer.PlaceTrace;
import com.tomtom.places.unicorn.pipeline.repository.RunDescriptorSupport;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;
import com.tomtom.places.unicorn.rundescriptor.ArtifactId;

public class PlaceTracer extends Configured implements Tool {

    public class PlaceKeyFn extends MapFn<Pair<String, SpecificRecordBase>, String> {

        @Override
        public String map(Pair<String, SpecificRecordBase> input) {
            return input.first();
        }
    }

    private final String runDescriptorPath;
    private final RunDescriptorSupport rds;

    public PlaceTracer(String runDescriptorPath) throws Exception {
        this(runDescriptorPath, HdfsTools.forDefaultFileSystem().getConfiguration());
    }

    public PlaceTracer(String runDescriptorPath, Configuration conf) throws Exception {
        this.runDescriptorPath = runDescriptorPath;
        rds = RunDescriptorSupport.loadFromFile(conf, runDescriptorPath);
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HdfsTools.forLocalFileSystem().getConfiguration();
        ToolRunner.run(conf, new PlaceTracer(args[0], conf), args);
    }

    public int run(String[] args) throws Exception {
        Pipeline pipeline = new MRPipeline(PlaceTracer.class, getConf());
        PipelineResult result = run(pipeline);
        return result.succeeded() ? 0 : -1;
    }

    public PipelineResult run(Pipeline pipeline) throws IOException {
        String mappedPlaces = rds.getOutputArtifactPath(ArtifactId.MAPPED_PLACES, true);
        List<String> dirs = HdfsTools.forDefaultFileSystem().listDirectories(mappedPlaces, ".*");

        System.out.println(mappedPlaces);
        System.out.println(dirs);

        for (String locality : dirs) {
            createTracking(locality, pipeline);
        }

        return pipeline.done();
    }

    @SuppressWarnings("unchecked")
    public void createTracking(String locality, Pipeline pipeline) throws IOException {
        PCollection<? extends SpecificRecordBase> mappedPlaces = readMappedPlaces(locality, pipeline);
        PCollection<Pair<String, SpecificRecordBase>> mapped = withKey("MAPPED_PLACES_KEY", mappedPlaces, NormalizedPlace.class);

        PCollection<? extends SpecificRecordBase> clusteredPlaces = readClusteredPlaces(locality, pipeline);
        PCollection<Pair<String, SpecificRecordBase>> clustered = withKey("CLUSTERED_PLACES_KEY", clusteredPlaces, ClusteredPlace.class);

        PCollection<? extends SpecificRecordBase> traceColl = readTraces(locality, pipeline);
        PCollection<Pair<String, SpecificRecordBase>> traces = withKey("TRACES_KEY", traceColl, Trace.class);

        PCollection<? extends SpecificRecordBase> archivePlaces = readArchivePlaces(locality, pipeline);
        PCollection<Pair<String, SpecificRecordBase>> archives = withKey("ARCHIVE_PLACES_KEY", archivePlaces, ArchivePlace.class);

        PCollection<Pair<String, SpecificRecordBase>> union = mapped.union(clustered).union(archives).union(traces);

        PGroupedTable<String, Pair<String, SpecificRecordBase>> groupByKey =
            union.by(new PlaceKeyFn(), Avros.strings()).groupByKey();

        PCollection<PlaceTrace> tracked = groupByKey.parallelDo(new PlaceTracerDoFn(), Avros.records(PlaceTrace.class));

        // 1) emit tracked info as Pair<ClusteredPlaceId, PlaceTrackInfo>
        // 2) read and emit archive places as Pair<ArchivePlaceId, Wrap ArchivePlace into PlackTrackInfo>
        // 3) how to emit and combine traces that were logged with ClusteredPlaceId?
        // union 1, 2 & 3 and groupByKey() then in process wrap all places together into PlaceTrackInfo and emit wrapped PlaceTrackInfo
    }

    private PCollection<Pair<String, SpecificRecordBase>> withKey(String operation, PCollection<? extends SpecificRecordBase> coll,
        Class<? extends SpecificRecordBase> clazz) {
        return coll.parallelDo(operation, new KeyDoFn(),
            Avros.pairs(Avros.strings(), Avros.records(clazz)));
    }

    private PCollection<? extends SpecificRecordBase> readMappedPlaces(String locality, Pipeline pipeline) {
        String mappedPlaces = rds.getOutputArtifactPath(ArtifactId.MAPPED_PLACES, true);
        return read(NormalizedPlace.class, mappedPlaces + "/" + locality, pipeline);
    }

    private PCollection<? extends SpecificRecordBase> readClusteredPlaces(String locality, Pipeline pipeline) {
        String clusteredPlaces = rds.getOutputArtifactPath(ArtifactId.CLUSTERED_PLACES, true);
        return read(ClusteredPlace.class, clusteredPlaces + "/" + locality, pipeline);
    }

    private PCollection<? extends SpecificRecordBase> readArchivePlaces(String locality, Pipeline pipeline) {
        String archivePlaces = rds.getOutputArtifactPath(ArtifactId.ARCHIVE_PLACES, true);
        return read(ArchivePlace.class, archivePlaces + "/" + locality + "/GP3_ARCHIVE", pipeline);
    }

    private PCollection<? extends SpecificRecordBase> readTraces(String locality, Pipeline pipeline) throws IOException {
        String tracePath = rds.getOutputArtifactPath(ArtifactId.TRACE_DUMP, true);

        Collection<File> dirs = FileUtils.listFiles(new File(tracePath), new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
        PCollection<Trace> traces = null;
        for (File dir : dirs) {
            if (traces == null) {
                traces = read(Trace.class, dir.getAbsolutePath(), pipeline);
            } else {
                traces = traces.union(read(Trace.class, dir.getAbsolutePath(), pipeline));
            }
        }
        return traces;
    }

    public static <T extends SpecificRecordBase> PCollection<T> read(Class<T> clazz, String path, Pipeline pipeline) {
        return pipeline.read(new AvroFileSource<T>(new Path(path), Avros.records(clazz)));
    }

    public static void print(PCollection<? extends SpecificRecordBase> mappedPlaces) {
        List<? extends SpecificRecordBase> places = Utils.materialize(mappedPlaces);
        for (SpecificRecordBase p : places) {
            System.out.println(p);
        }
    }
}
