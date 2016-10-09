package com.tomtom.places.archive.checker.checks;

import java.util.Arrays;
import java.util.List;

import com.tomtom.places.archive.checker.criteria.ArchiveCriteria;
import com.tomtom.places.archive.checker.criteria.CoordiantesInRangeCriteria;
import com.tomtom.places.archive.checker.criteria.GdfCodeCriteria;
import com.tomtom.places.archive.checker.criteria.OfficialNameCriteria;

public class ArchiveChecksFactory {

    public static final List<ArchiveCheck> getChecks() {
        return Arrays.<ArchiveCheck>asList(new OccurrenceCheck("CI_1.47", 1, Arrays.<ArchiveCriteria>asList(new GdfCodeCriteria("7321"),
            new CoordiantesInRangeCriteria(-76.186174, 42.608928, 0.005),
            new OfficialNameCriteria("Cortland Regional Medical Center", 75))));
    }
}
