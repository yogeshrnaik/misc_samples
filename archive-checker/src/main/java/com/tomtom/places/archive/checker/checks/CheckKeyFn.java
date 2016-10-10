package com.tomtom.places.archive.checker.checks;

import com.cloudera.crunch.MapFn;
import com.cloudera.crunch.Pair;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class CheckKeyFn extends MapFn<Pair<String, ArchivePlace>, String> {

    private static final long serialVersionUID = 83734979913240904L;

    @Override
    public String map(Pair<String, ArchivePlace> result) {
        return result.first();
    }
}
