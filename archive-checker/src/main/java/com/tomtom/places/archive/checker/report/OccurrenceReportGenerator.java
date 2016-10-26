package com.tomtom.places.archive.checker.report;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tomtom.places.archive.checker.checks.ArchiveChecksFactory;
import com.tomtom.places.archive.checker.checks.OccurrenceCheck;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.archive.checker.result.OccurrenceCheckResult;

public class OccurrenceReportGenerator extends CheckReportGenerator {

    @Override
    public CheckReport getReport(String checkId, Iterable<CheckResult> checkResults) {
        Iterable<CheckResult> relevant = getRelevantResults(checkResults);
        List<CheckResult> results = Lists.newArrayList(relevant);
        OccurrenceCheck check = (OccurrenceCheck)ArchiveChecksFactory.getCheck(checkId);
        String message = getMessage(check, results);
        return new OccurrenceCheckReport(checkId, message, results);
    }

    private Iterable<CheckResult> getRelevantResults(Iterable<CheckResult> checkResults) {
        Iterable<CheckResult> results = Iterables.filter(checkResults, new Predicate<CheckResult>() {

            public boolean apply(CheckResult input) {
                if (input instanceof OccurrenceCheckResult) {
                    OccurrenceCheckResult result = (OccurrenceCheckResult)input;
                    return result.getNoOfOccurrences() > 0;
                }
                return false;
            }
        });
        return results;
    }

    private String getMessage(OccurrenceCheck check, List<CheckResult> results) {
        if (results.size() == check.getExpectedOccurences()) {
            return String.format("Expected number of occurrences [%s] of POI match.", check.getExpectedOccurences());
        }
        return String.format("Expected number of occurrences of POI [%s] do not match with actual [%d].",
            check.getExpectedOccurences(), results.size());
    }
}
