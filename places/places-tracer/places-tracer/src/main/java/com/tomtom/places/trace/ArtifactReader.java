package com.tomtom.places.trace;

import java.io.IOException;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.hadoop.fs.Path;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.io.avro.AvroFileSource;
import com.cloudera.crunch.types.avro.Avros;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.clustered.ClusteredPlace;
import com.tomtom.places.unicorn.domain.avro.normalized.NormalizedPlace;
import com.tomtom.places.unicorn.domain.avro.trace.Phase;
import com.tomtom.places.unicorn.domain.avro.trace.Trace;
import com.tomtom.places.unicorn.pipeline.repository.RunDescriptorSupport;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;
import com.tomtom.places.unicorn.pipelineutil.MultiPathAvroFileSource;
import com.tomtom.places.unicorn.rundescriptor.ArtifactId;

public class ArtifactReader {

    private final String runDescriptorPath;
    private final RunDescriptorSupport rds;
    private final HdfsTools hdfs;

    public ArtifactReader(String runDescriptorPath, RunDescriptorSupport rds, HdfsTools hdfs) {
        this.runDescriptorPath = runDescriptorPath;
        this.rds = rds;
        this.hdfs = hdfs;
    }

    public PCollection<NormalizedPlace> readMappedPlaces(String locality, Pipeline pipeline) throws Exception {
        return read(locality, ArtifactId.MAPPED_PLACES, NormalizedPlace.class, pipeline);
    }

    public PCollection<ClusteredPlace> readClusteredPlaces(String locality, Pipeline pipeline) throws Exception {
        return read(locality, ArtifactId.CLUSTERED_PLACES, ClusteredPlace.class, pipeline);
    }

    public PCollection<ArchivePlace> readArchivePlaces(String locality, Pipeline pipeline) throws Exception {
        String artifactPath = rds.getOutputArtifactPath(ArtifactId.ARCHIVE_PLACES, true) + "/" + locality + "/GP3_ARCHIVE";
        return read(ArchivePlace.class, artifactPath, pipeline);
    }

    public PCollection<Trace> readTraces(String locality, Pipeline pipeline) throws Exception {
        String tracesPath = rds.getOutputArtifactPath(ArtifactId.TRACE_DUMP, true) + "/traces";

        MultiPathAvroFileSource<Trace> source = new MultiPathAvroFileSource(Avros.records(Trace.class), true);
        addPathIfExists(tracesPath + "/_SHARED/Intake", source);
        for (Phase phase : Phase.values()) {
            addPathIfExists(tracesPath + "/" + locality + "/" + phase, source);
        }
        return pipeline.read(source);
    }

    private void addPathIfExists(String path, MultiPathAvroFileSource<Trace> source) throws IOException {
        if (hdfs.exists(path)) {
            source.addPath(new Path(path));
        }
    }

    private <T extends SpecificRecordBase> PCollection<T> read(String locality, ArtifactId artifact, Class<T> clazz,
        Pipeline pipeline) throws Exception {
        String artifactPath = rds.getOutputArtifactPath(artifact, true) + "/" + locality;
        return read(clazz, artifactPath, pipeline);
    }

    private <T extends SpecificRecordBase> PCollection<T> read(Class<T> clazz, String path, Pipeline pipeline) {
        AvroFileSource<T> source = new AvroFileSource<T>(new Path(path), Avros.records(clazz));
        return pipeline.read(source);
    }
}
