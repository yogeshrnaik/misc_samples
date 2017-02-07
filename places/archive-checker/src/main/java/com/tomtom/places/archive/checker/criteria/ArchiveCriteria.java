package com.tomtom.places.archive.checker.criteria;

import java.io.Serializable;

import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.archive.POI;
import com.tomtom.places.unicorn.domain.productized.archive.ArchivePlaces;

public abstract class ArchiveCriteria implements Serializable {

    private static final long serialVersionUID = 215347278772639856L;

    public abstract boolean isCriteriaMatch(ArchivePlace place);

    protected POI getPOI(ArchivePlace place) {
        return ArchivePlaces.getPoi(place);
    }
}
