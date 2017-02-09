package com.tomtom.places.trace;

import com.cloudera.crunch.MapFn;
import com.cloudera.crunch.Pair;
import com.tomtom.places.unicorn.domain.avro.tracer.PlaceTrace;

public class PlaceKeyFn extends MapFn<Pair<String, PlaceTrace>, String> {

    private static final long serialVersionUID = 5115898060092878039L;

    @Override
    public String map(Pair<String, PlaceTrace> input) {
        return input.first();
    }
}
