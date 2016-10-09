package com.tomtom.places.archive.checker.checks;

import java.util.List;

import com.tomtom.places.archive.checker.criteria.ArchiveCriteria;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.archive.checker.result.OccurrenceCheckResult;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class OccurrenceCheck extends ArchiveCheck {

    private final int expectedOccurences;

    public OccurrenceCheck(String checkId, int expectedOccurences, List<ArchiveCriteria> criteria) {
        super(checkId, criteria);
        this.expectedOccurences = expectedOccurences;
    }

    @Override
    public CheckResult check(ArchivePlace place) {
        return new OccurrenceCheckResult(this, place);
    }
}
