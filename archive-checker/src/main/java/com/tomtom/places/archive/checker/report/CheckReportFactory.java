package com.tomtom.places.archive.checker.report;

public class CheckReportFactory {

    public static CheckReportGenerator getReportGenerator(String checkId) {
        return new OccurrenceReportGenerator();
    }
}
