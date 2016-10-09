package com.tomtom.places.archive.checker.report;

import java.util.List;

import com.google.common.collect.Lists;
import com.tomtom.places.archive.checker.result.CheckResult;

public class OccurrenceReportGenerator implements CheckReportGenerator {

    public CheckReport getReport(String checkId, Iterable<CheckResult> validationResults) {
        List<CheckResult> results = Lists.newArrayList(validationResults);
        return new OccurrenceCheckReport(checkId, results);
    }

}
