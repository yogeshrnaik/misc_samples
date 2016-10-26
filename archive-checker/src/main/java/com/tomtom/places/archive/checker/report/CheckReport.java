package com.tomtom.places.archive.checker.report;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.Writable;

import com.google.common.collect.Lists;
import com.tomtom.places.archive.checker.result.CheckResult;

public class CheckReport implements Writable {

    private static final long serialVersionUID = -821292876090056232L;

    private String checkId;
    private List<CheckResult> results;

    protected CheckReport(String checkId, List<CheckResult> results) {
        this.checkId = checkId;
        this.results = results;
    }

    public void write(DataOutput out) throws IOException {
        out.writeChars(checkId);
        out.writeInt(results.size());
        for (CheckResult result : results) {
            result.write(out);
        }
    }

    public void readFields(DataInput in) throws IOException {
        checkId = in.readLine();
        int size = in.readInt();

        List<CheckResult> results = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            CheckResult result = new CheckResult(null, null);
            result.readFields(in);
            results.add(result);
        }
    }
}
