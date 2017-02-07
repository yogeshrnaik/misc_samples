package com.tomtom.places.trace;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang3.StringUtils;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.cloudera.crunch.Pair;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.clustered.ClusteredPlace;
import com.tomtom.places.unicorn.domain.avro.composite.CompositePlace;
import com.tomtom.places.unicorn.domain.avro.normalized.NormalizedPlace;
import com.tomtom.places.unicorn.domain.avro.source.AVUUID;
import com.tomtom.places.unicorn.domain.avro.trace.Trace;

public class KeyDoFn<S extends SpecificRecordBase> extends DoFn<S, Pair<String, SpecificRecordBase>> {

    @Override
    public void process(S input, Emitter<Pair<String, SpecificRecordBase>> emitter) {
        if (input instanceof NormalizedPlace) {
            NormalizedPlace place = (NormalizedPlace)input;
            emitter.emit(Pair.of(place.getDeliveryPlaceId().toString(), (SpecificRecordBase)NormalizedPlace.newBuilder(place).build()));
        } else if (input instanceof ArchivePlace) {
            ArchivePlace place = (ArchivePlace)input;
            for (AVUUID id : place.getDeliveryPlaceIds()) {
                emitter.emit(Pair.of(id.toString(), (SpecificRecordBase)place));
            }
        } else if (input instanceof ClusteredPlace) {
            ClusteredPlace place = ClusteredPlace.newBuilder((ClusteredPlace)input).build();
            for (CompositePlace cp : place.getMatchingPlaces()) {
                emitter.emit(Pair.of(cp.getMappedPlace().getDeliveryPlaceId().toString(),
                    (SpecificRecordBase)place));
            }
            if (place.getRejectedPlaces() != null) {
                for (CompositePlace cp : place.getRejectedPlaces()) {
                    emitter.emit(Pair.of(cp.getMappedPlace().getDeliveryPlaceId().toString(), (SpecificRecordBase)place));
                }
            }
        } else if (input instanceof Trace) {
            Trace trace = Trace.newBuilder((Trace)input).build();
            if (StringUtils.isNotBlank(trace.getObjectId()) && trace.getObjectId().toString().contains("mostSigBits")) {
                emitter.emit(Pair.of(trace.getObjectId().toString(), (SpecificRecordBase)trace));
            }
            if (trace.getOriginObjectIds() != null) {
                for (CharSequence id : trace.getOriginObjectIds()) {
                    if (StringUtils.isNotBlank(id)) {
                        emitter.emit(Pair.of(id.toString(), (SpecificRecordBase)trace));
                    }
                }
            }
        }
    }
}
