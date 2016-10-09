package com.tomtom.places.archive.checker.report;

import java.util.List;

import com.tomtom.places.archive.checker.result.CheckResult;

public class OccurrenceCheckReport extends CheckReport {

    private static final long serialVersionUID = -2281661406852236352L;

    private final List<CheckResult> results;

    protected OccurrenceCheckReport(String checkId, List<CheckResult> results) {
        super(checkId);
        this.results = results;
    }

}
