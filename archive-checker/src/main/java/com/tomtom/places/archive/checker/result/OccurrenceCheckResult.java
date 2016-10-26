package com.tomtom.places.archive.checker.result;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.tomtom.places.archive.checker.checks.ArchiveCheck;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class OccurrenceCheckResult extends CheckResult {

    private int noOfOccurrences;

    public OccurrenceCheckResult(int noOfOccurrences, ArchiveCheck check, ArchivePlace validatedPlace) {
        super(check, validatedPlace);
        this.noOfOccurrences = noOfOccurrences;
    }

    public int getNoOfOccurrences() {
        return noOfOccurrences;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        out.writeInt(noOfOccurrences);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        super.readFields(in);
        noOfOccurrences = in.readInt();
    }

}
