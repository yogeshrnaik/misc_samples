package com.tomtom.places.archive.checker.checks;

import com.cloudera.crunch.MapFn;
import com.tomtom.places.archive.checker.result.CheckResult;

public class CheckKeyFn extends MapFn<CheckResult, String> {

    private static final long serialVersionUID = 83734979913240904L;

    @Override
    public String map(CheckResult result) {
        return result.getCheck().getCheckId();
    }
}
