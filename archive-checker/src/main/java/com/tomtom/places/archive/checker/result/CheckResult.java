package com.tomtom.places.archive.checker.result;

import java.io.Serializable;

import com.tomtom.places.archive.checker.checks.ArchiveCheck;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public abstract class CheckResult implements Serializable {

    private static final long serialVersionUID = -3677049151813639601L;

    protected final ArchiveCheck check;
    protected final ArchivePlace validatedPlace;

    public CheckResult(ArchiveCheck check, ArchivePlace validatedPlace) {
        this.check = check;
        this.validatedPlace = validatedPlace;
    }

    public ArchiveCheck getCheck() {
        return check;
    }

    public ArchivePlace getValidatedPlace() {
        return validatedPlace;
    }

}
