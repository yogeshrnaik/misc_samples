package com.tomtom.places.archive.checker.report;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.cloudera.crunch.Pair;
import com.tomtom.places.archive.checker.result.CheckResult;

public class ValidationResultsConsolidator extends DoFn<Pair<String, Iterable<CheckResult>>, CheckReport> {

    private static final long serialVersionUID = -5957091545488466206L;

    @Override
    public void process(Pair<String, Iterable<CheckResult>> input, Emitter<CheckReport> emitter) {
        String checkId = input.first();
        CheckReportGenerator reportGenerator = CheckReportFactory.getReportGenerator(checkId);
        emitter.emit(reportGenerator.getReport(checkId, input.second()));
    }

}
