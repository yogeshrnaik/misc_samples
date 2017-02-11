package com.tomtom.places.trace;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang3.StringUtils;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.cloudera.crunch.Pair;
import com.google.common.collect.Lists;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.clustered.ClusteredPlace;
import com.tomtom.places.unicorn.domain.avro.composite.CompositePlace;
import com.tomtom.places.unicorn.domain.avro.normalized.NormalizedPlace;
import com.tomtom.places.unicorn.domain.avro.source.AVUUID;
import com.tomtom.places.unicorn.domain.avro.trace.Trace;
import com.tomtom.places.unicorn.domain.avro.tracer.PlaceTrace;

public class ArtifactWrapperWithKeyDoFn<S extends SpecificRecordBase> extends DoFn<S, Pair<String, PlaceTrace>> {

    @Override
    public void process(S input, Emitter<Pair<String, PlaceTrace>> emitter) {
        PlaceTrace placeTrace = PlaceTrace.newBuilder().build();

        if (input instanceof NormalizedPlace) {
            NormalizedPlace place = (NormalizedPlace)input;
            placeTrace.setMappedPlace(NormalizedPlace.newBuilder(place).build());
            emitter.emit(Pair.of(place.getDeliveryPlaceId().toString(), placeTrace));
        } else if (input instanceof ClusteredPlace) {
            ClusteredPlace place = ClusteredPlace.newBuilder((ClusteredPlace)input).build();
            placeTrace.setClusteredPlace(ClusteredPlace.newBuilder(place).build());
            for (CompositePlace cp : place.getMatchingPlaces()) {
                emitter.emit(Pair.of(cp.getMappedPlace().getDeliveryPlaceId().toString(), placeTrace));
            }
            if (place.getRejectedPlaces() != null) {
                for (CompositePlace cp : place.getRejectedPlaces()) {
                    emitter.emit(Pair.of(cp.getMappedPlace().getDeliveryPlaceId().toString(), placeTrace));
                }
            }
        } else if (input instanceof Trace) {
            Trace trace = Trace.newBuilder((Trace)input).build();
            placeTrace.setTraces(Lists.newArrayList(Trace.newBuilder(trace).build()));
            if (StringUtils.isNotBlank(trace.getObjectId()) && trace.getObjectId().toString().contains("mostSigBits")) {
                emitter.emit(Pair.of(trace.getObjectId().toString(), placeTrace));
            }
            if (trace.getOriginObjectIds() != null) {
                for (CharSequence id : trace.getOriginObjectIds()) {
                    if (StringUtils.isNotBlank(id)) {
                        emitter.emit(Pair.of(id.toString(), placeTrace));
                    }
                }
            }
        } else if (input instanceof ArchivePlace) {
            ArchivePlace place = (ArchivePlace)input;
            placeTrace.setArchivePlace(ArchivePlace.newBuilder(place).build());
            for (AVUUID id : place.getDeliveryPlaceIds()) {
                emitter.emit(Pair.of(id.toString(), placeTrace));
            }
        } /*else if (input instanceof PlaceTrace) {
            PlaceTrace place = (PlaceTrace)input;
            if (placeTrace.getClusteredPlace() != null) {
                emitter.emit(Pair.of(placeTrace.getClusteredPlace().getClusteredPlaceId().toString(), (SpecificRecordBase)placeTrace));
            }
        }*/
    }
}
