package com.tomtom.places.archive.checker.criteria;

import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class GdfCodeCriteria extends ArchiveCriteria {

    private static final long serialVersionUID = 5001420781481114488L;

    private final String gdfCodeToMatch;

    public GdfCodeCriteria(String gdfCode) {
        gdfCodeToMatch = gdfCode;
    }

    @Override
    public boolean isCriteriaMatch(ArchivePlace place) {
        return gdfCodeToMatch.equals(getPOI(place).getGdfFeatureCode().toString());
    }

    @Override
    public String toString() {
        return "GdfCodeCriteria [gdfCodeToMatch=" + gdfCodeToMatch + "]";
    }
}
