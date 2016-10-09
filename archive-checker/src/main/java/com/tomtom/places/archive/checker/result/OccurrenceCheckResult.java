package com.tomtom.places.archive.checker.result;

import com.tomtom.places.archive.checker.checks.ArchiveCheck;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class OccurrenceCheckResult extends CheckResult {

    private static final long serialVersionUID = -1728507443555711606L;

    public OccurrenceCheckResult(ArchiveCheck check, ArchivePlace validatedPlace) {
        super(check, validatedPlace);
    }

}
