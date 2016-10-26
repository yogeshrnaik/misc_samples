package com.tomtom.places.archive.checker.report;

import java.util.List;

import com.tomtom.places.archive.checker.result.CheckResult;

public class OccurrenceCheckReport extends CheckReport {

    private final String message;

    protected OccurrenceCheckReport(String checkId, List<CheckResult> results, String message) {
        super(checkId, results);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
