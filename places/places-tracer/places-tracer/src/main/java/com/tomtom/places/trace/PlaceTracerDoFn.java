package com.tomtom.places.trace;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.cloudera.crunch.Pair;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.clustered.ClusteredPlace;
import com.tomtom.places.unicorn.domain.avro.normalized.NormalizedPlace;
import com.tomtom.places.unicorn.domain.avro.trace.Trace;
import com.tomtom.places.unicorn.domain.avro.tracer.PlaceTrace;

public class PlaceTracerDoFn extends DoFn<Pair<String, Iterable<Pair<String, PlaceTrace>>>, PlaceTrace> {

    private static final long serialVersionUID = -956735151112380467L;

    @Override
    public void process(Pair<String, Iterable<Pair<String, PlaceTrace>>> input, Emitter<PlaceTrace> emitter) {
        PlaceTrace placeTrace = createPlaceTrace(input);
        sortTracesByTimestamp(placeTrace);
        if (placeTrace.getMappedPlace() != null) {
            emitter.emit(placeTrace);
            // System.out.println(placeTrace);
        }
    }

    private PlaceTrace createPlaceTrace(Pair<String, Iterable<Pair<String, PlaceTrace>>> input) {
        PlaceTrace placeTrace = PlaceTrace.newBuilder().setTraces(Lists.<Trace>newArrayList()).build();
        Set<Trace> uniqueTraces = Sets.newHashSet();
        fillPlaceTrace(input, placeTrace, uniqueTraces);
        placeTrace.getTraces().addAll(uniqueTraces);
        return placeTrace;
    }

    private void fillPlaceTrace(Pair<String, Iterable<Pair<String, PlaceTrace>>> input, PlaceTrace placeTrace,
        Set<Trace> uniqueTraces) {
        for (Pair<String, PlaceTrace> pair : input.second()) {
            PlaceTrace record = pair.second();

            if (record.getMappedPlace() != null) {
                placeTrace.setMappedPlace(NormalizedPlace.newBuilder(record.getMappedPlace()).build());
            } else if (record.getClusteredPlace() != null) {
                placeTrace.setClusteredPlace(ClusteredPlace.newBuilder(record.getClusteredPlace()).build());
            } else if (record.getArchivePlace() != null) {
                placeTrace.setArchivePlace(ArchivePlace.newBuilder(record.getArchivePlace()).build());
            } else if (CollectionUtils.isNotEmpty(record.getTraces())) {
                for (Trace trace : record.getTraces()) {
                    uniqueTraces.add(Trace.newBuilder(trace).build());
                }
            }
        }
    }

    private void sortTracesByTimestamp(PlaceTrace placeTrace) {
        // sort the traces list in ascending order of timestamp
        Collections.sort(placeTrace.getTraces(), new Comparator<Trace>() {

            public int compare(Trace t1, Trace t2) {
                return t1.getTimestamp().intValue() - t2.getTimestamp().intValue();
            }
        });
    }
}