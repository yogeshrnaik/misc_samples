package com.tomtom.places.archive.checker.result;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import com.tomtom.places.archive.checker.checks.ArchiveCheck;
import com.tomtom.places.archive.checker.checks.ArchiveChecksFactory;
import com.tomtom.places.archive.checker.util.Utils;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.util.SerializationUtil;

public class CheckResult implements Writable {

    protected ArchiveCheck check;
    private boolean isCheckedPlacePresent;
    protected ArchivePlace checkedPlace;

    public CheckResult(ArchiveCheck check, ArchivePlace checkedPlace) {
        this.check = check;
        this.checkedPlace = checkedPlace;
        isCheckedPlacePresent = checkedPlace != null;
    }

    public ArchiveCheck getCheck() {
        return check;
    }

    public ArchivePlace getCheckedPlace() {
        return checkedPlace;
    }

    public void write(DataOutput out) throws IOException {
        out.writeChars(check.getCheckId());
        out.writeBoolean(isCheckedPlacePresent);
        if (isCheckedPlacePresent) {
            out.writeChars(SerializationUtil.convertToJson(checkedPlace));
        }

    }

    public void readFields(DataInput in) throws IOException {
        check = ArchiveChecksFactory.getCheck(in.readLine());
        isCheckedPlacePresent = in.readBoolean();
        if (isCheckedPlacePresent) {
            checkedPlace = Utils.convertToAvroObject(ArchivePlace.SCHEMA$, in.readLine());
        } else {
            checkedPlace = null;
        }
    }

    @Override
    public String toString() {
        return checkedPlace != null ? checkedPlace.toString() : null;
    }
}
