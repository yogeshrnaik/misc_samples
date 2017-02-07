package com.tomtom.places.archive.checker.checks;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.tomtom.places.archive.checker.criteria.ArchiveCriteria;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.archive.POI;
import com.tomtom.places.unicorn.domain.productized.archive.ArchivePlaces;

public abstract class ArchiveCheck {

    protected final String checkId;
    protected final List<ArchiveCriteria> criteria;

    public ArchiveCheck(String checkId, ArchiveCriteria... criteria) {
        this.checkId = checkId;
        this.criteria = Arrays.asList(criteria);
    }

    public String getCheckId() {
        return checkId;
    }

    public boolean isApplicable(final ArchivePlace place) {
        return CollectionUtils.isEmpty(criteria) || Iterables.all(criteria, new Predicate<ArchiveCriteria>() {

            public boolean apply(ArchiveCriteria input) {
                return input.isCriteriaMatch(place);
            }
        });
    }

    protected CheckResult check(ArchivePlace place) {
        return isApplicable(place) ? executeCheck(place) : skipCheck(place);
    }

    protected abstract CheckResult executeCheck(ArchivePlace place);

    protected CheckResult skipCheck(ArchivePlace place) {
        return null;
    }

    protected POI getPOI(ArchivePlace place) {
        return ArchivePlaces.getPoi(place);
    }

    @Override
    public String toString() {
        return "checkId=" + checkId + ", criteria=" + criteria;
    }

}
