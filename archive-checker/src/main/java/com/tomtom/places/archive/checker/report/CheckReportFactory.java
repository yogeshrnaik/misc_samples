package com.tomtom.places.archive.checker.report;

import java.util.HashMap;
import java.util.Map;

public class CheckReportFactory {

    private static final Map<String, CheckReportGenerator> reportGenerators = new HashMap<String, CheckReportGenerator>();
    static {
        reportGenerators.put("CI_1.47", new OccurrenceReportGenerator());
        reportGenerators.put("SRC_1.18", new CheckReportGenerator());
    }

    public static CheckReportGenerator getReportGenerator(String checkId) {
        return reportGenerators.get(checkId);
    }
}
