package com.tomtom.places.archive.checker.report;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.cloudera.crunch.Pair;
import com.tomtom.places.archive.checker.checks.ArchiveChecksFactory;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class CheckResultsWriter extends DoFn<Pair<String, Iterable<Pair<String, ArchivePlace>>>, String> {

    private static final long serialVersionUID = -5957091545488466206L;

    @Override
    public void process(Pair<String, Iterable<Pair<String, ArchivePlace>>> input, Emitter<String> emitter) {
        String checkId = input.first();
        System.out.println(ArchiveChecksFactory.getCheck(checkId));

        for (Pair<String, ArchivePlace> pair : input.second()) {
            System.out.println(pair.second());
        }
        System.out.println("---------------------------------------------------");
        emitter.emit(checkId);
    }
}
