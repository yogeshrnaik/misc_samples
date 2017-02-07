package com.tomtom.places.archive.checker.report;

import java.util.List;

import com.google.common.collect.Lists;
import com.tomtom.places.archive.checker.result.CheckResult;

public class CheckReportGenerator {

    public CheckReport getReport(String checkId, Iterable<CheckResult> checkResults) {
        List<CheckResult> results = Lists.newArrayList(checkResults);
        return new CheckReport(checkId, results);
    }
}
