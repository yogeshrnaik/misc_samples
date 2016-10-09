package com.tomtom.places.archive.checker.checks;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.tomtom.places.archive.checker.criteria.ArchiveCriteria;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public abstract class ArchiveCheck {

    protected final String checkId;
    protected final List<ArchiveCriteria> criteria;

    public ArchiveCheck(String checkId, List<ArchiveCriteria> criteria) {
        this.checkId = checkId;
        this.criteria = criteria;
    }

    public String getCheckId() {
        return checkId;
    }

    public boolean isApplicable(final ArchivePlace place) {
        return Iterables.all(criteria, new Predicate<ArchiveCriteria>() {

            public boolean apply(ArchiveCriteria input) {
                return input.isCriteriaMatch(place);
            }
        });
    }

    public abstract CheckResult check(ArchivePlace place);
}
