package com.tomtom.places.archive.checker.checks;

import com.tomtom.places.archive.checker.criteria.ArchiveCriteria;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.archive.checker.result.OccurrenceCheckResult;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class OccurrenceCheck extends ArchiveCheck {

    private final int expectedOccurences;

    public OccurrenceCheck(String checkId, int expectedOccurences, ArchiveCriteria... criteria) {
        super(checkId, criteria);
        this.expectedOccurences = expectedOccurences;
    }

    @Override
    protected CheckResult executeCheck(ArchivePlace place) {
        return new OccurrenceCheckResult(1, this, place);
    }

    @Override
    protected CheckResult skipCheck(ArchivePlace place) {
        return new OccurrenceCheckResult(0, this, place);
    }

    public int getExpectedOccurences() {
        return expectedOccurences;
    }

    @Override
    public String toString() {
        return "OccurrenceCheck [expectedOccurences=" + expectedOccurences + ", " + super.toString() + "]";
    }
}
