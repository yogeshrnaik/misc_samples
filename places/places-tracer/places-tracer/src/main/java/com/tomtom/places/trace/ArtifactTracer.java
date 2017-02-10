package com.tomtom.places.trace;

import org.apache.avro.specific.SpecificRecordBase;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.Pair;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.types.avro.Avros;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.clustered.ClusteredPlace;
import com.tomtom.places.unicorn.domain.avro.normalized.NormalizedPlace;
import com.tomtom.places.unicorn.domain.avro.trace.Trace;
import com.tomtom.places.unicorn.domain.avro.tracer.PlaceTrace;
import com.tomtom.places.unicorn.pipeline.repository.RunDescriptorSupport;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;
import com.tomtom.places.unicorn.rundescriptor.ArtifactId;

public class ArtifactTracer {

    private final String runDescriptorPath;
    private final RunDescriptorSupport rds;
    private final ArtifactReader reader;

    public ArtifactTracer(String runDescriptorPath, RunDescriptorSupport rds, HdfsTools hdfs) {
        this.runDescriptorPath = runDescriptorPath;
        this.rds = rds;
        reader = new ArtifactReader(runDescriptorPath, rds, hdfs);
    }

    public PCollection<Pair<String, PlaceTrace>> getAllArtifacts(String locality, Pipeline pipeline) throws Exception {
        PCollection<NormalizedPlace> mappedPlaces = reader.readMappedPlaces(locality, pipeline);
        PCollection<Pair<String, PlaceTrace>> mapped = wrapArtifact(locality, ArtifactId.MAPPED_PLACES, mappedPlaces, pipeline);

        PCollection<ClusteredPlace> clusteredPlaces = reader.readClusteredPlaces(locality, pipeline);
        PCollection<Pair<String, PlaceTrace>> clustered = wrapArtifact(locality, ArtifactId.CLUSTERED_PLACES, clusteredPlaces, pipeline);

        PCollection<ArchivePlace> archivePlaces = reader.readArchivePlaces(locality, pipeline);
        PCollection<Pair<String, PlaceTrace>> archives = wrapArtifact(locality, ArtifactId.ARCHIVE_PLACES, archivePlaces, pipeline);

        PCollection<Trace> allTraces = reader.readTraces(locality, pipeline);
        PCollection<Pair<String, PlaceTrace>> traces = wrapArtifact(locality, ArtifactId.TRACE_DUMP, allTraces, pipeline);

        return mapped.union(clustered).union(archives).union(traces);
    }

    private <T extends SpecificRecordBase> PCollection<Pair<String, PlaceTrace>>
        wrapArtifact(String locality, ArtifactId artifactId, PCollection<T> artifact, Pipeline pipeline) throws Exception {
        return artifact.parallelDo(artifactId.toString(), new KeyDoFn(), Avros.pairs(Avros.strings(), Avros.records(PlaceTrace.class)));
    }
}
