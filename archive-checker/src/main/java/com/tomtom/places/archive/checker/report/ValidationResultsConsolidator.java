package com.tomtom.places.archive.checker.report;

import java.util.Collection;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.cloudera.crunch.Pair;
import com.google.common.collect.Lists;
import com.tomtom.places.archive.checker.checks.ArchiveChecksFactory;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class ValidationResultsConsolidator extends
    DoFn<Pair<String, Iterable<Pair<String, ArchivePlace>>>, Pair<String, Collection<ArchivePlace>>> {

    private static final long serialVersionUID = -5957091545488466206L;

    @Override
    public void process(Pair<String, Iterable<Pair<String, ArchivePlace>>> input, Emitter<Pair<String, Collection<ArchivePlace>>> emitter) {
        String checkId = input.first();
        System.out.println(ArchiveChecksFactory.getCheck(checkId));
        Collection<ArchivePlace> places = Lists.newArrayList();
        for (Pair<String, ArchivePlace> pair : input.second()) {
            System.out.println(pair.second());
            places.add(pair.second());
        }
        System.out.println("---------------------------------------------------");
        emitter.emit(Pair.of(checkId, places));
    }
}
