package com.tomtom.places.archive.checker.report;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.cloudera.crunch.Pair;
import com.tomtom.places.archive.checker.checks.ArchiveChecksFactory;
import com.tomtom.places.archive.checker.result.CheckResult;

public class CheckResultsWriter extends DoFn<Pair<String, Iterable<CheckResult>>, String> {

    private static final long serialVersionUID = -5957091545488466206L;

    @Override
    public void process(Pair<String, Iterable<CheckResult>> input, Emitter<String> emitter) {
        String checkId = input.first();
        System.out.println(ArchiveChecksFactory.getCheck(checkId));

        for (CheckResult result : input.second()) {
            System.out.println(result);
        }
        System.out.println();
        emitter.emit(checkId);
    }
}
