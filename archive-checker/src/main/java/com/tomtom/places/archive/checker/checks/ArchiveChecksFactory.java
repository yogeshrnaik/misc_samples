package com.tomtom.places.archive.checker.checks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.tomtom.places.archive.checker.criteria.CoordiantesInRangeCriteria;
import com.tomtom.places.archive.checker.criteria.ForAllCriteria;
import com.tomtom.places.archive.checker.criteria.GdfCodeCriteria;
import com.tomtom.places.archive.checker.criteria.OfficialNameCriteria;

public class ArchiveChecksFactory {

    private static final Map<String, ArchiveCheck> checks = new LinkedHashMap<String, ArchiveCheck>();

    static {
        checks.put("CI_1.47", new OccurrenceCheck("CI_1.47", 1,
            new GdfCodeCriteria("7321"),
            new CoordiantesInRangeCriteria(-76.186174, 42.608928, 0.005),
            new OfficialNameCriteria("Cortland Regional Medical Center", 75)));

        checks.put("SRC_1.18", new StreetNamesCheck("SRC_1.18", new ForAllCriteria()));
    }

    public static final List<ArchiveCheck> getChecks() {
        return Lists.newArrayList(checks.values());
    }

    public static ArchiveCheck getCheck(String checkId) {
        return checks.get(checkId);
    }
}
