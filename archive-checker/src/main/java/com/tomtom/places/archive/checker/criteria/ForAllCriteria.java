package com.tomtom.places.archive.checker.criteria;

import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class ForAllCriteria extends ArchiveCriteria {

    private static final long serialVersionUID = -9123240583647553190L;

    @Override
    public boolean isCriteriaMatch(ArchivePlace place) {
        return true;
    }

}
