package com.tomtom.places.archive.checker.report;

import com.tomtom.places.archive.checker.result.CheckResult;

interface CheckReportGenerator {

    CheckReport getReport(String checkId, Iterable<CheckResult> validationResults);
}
