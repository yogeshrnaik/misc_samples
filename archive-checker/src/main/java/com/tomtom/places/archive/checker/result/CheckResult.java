package com.tomtom.places.archive.checker.result;

import java.io.Serializable;

import com.tomtom.places.archive.checker.checks.ArchiveCheck;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class CheckResult implements Serializable {

    private static final long serialVersionUID = -3677049151813639601L;

    protected final ArchiveCheck check;
    protected final ArchivePlace checkedPlace;

    public CheckResult(ArchiveCheck check, ArchivePlace validatedPlace) {
        this.check = check;
        checkedPlace = validatedPlace;
    }

    public ArchiveCheck getCheck() {
        return check;
    }

    public ArchivePlace getCheckedPlace() {
        return checkedPlace;
    }

}
