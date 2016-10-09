package com.tomtom.places.archive.checker.criteria;

import com.tomtom.places.archive.checker.util.Utils;
import com.tomtom.places.unicorn.domain.avro.archive.ArchiveName;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.archive.POI;

public class OfficialNameCriteria extends ArchiveCriteria {

    private static final long serialVersionUID = -8018094787226474224L;

    private final String officialNameToMatch;
    private final int similarityThreshold;

    public OfficialNameCriteria(String officialNameToMatch, int similarityThreshold) {
        this.officialNameToMatch = officialNameToMatch;
        this.similarityThreshold = similarityThreshold;
    }

    @Override
    public boolean isCriteriaMatch(ArchivePlace place) {
        return coordiantesInRange(getPOI(place));
    }

    private boolean coordiantesInRange(POI poi) {
        return officialNameMatches(poi, officialNameToMatch);

    }

    private boolean officialNameMatches(POI poi, String nameToMatch) {
        for (ArchiveName name : poi.getOfficialNames()) {
            if (Utils.getScaledLevenshteinDistance(nameToMatch, name.getName()) >= similarityThreshold) {
                return true;
            }
        }
        return false;
    }
}
