package com.tomtom.places.archive.checker.checks;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class ApplyArchiveChecks extends DoFn<ArchivePlace, CheckResult> {

    private static final long serialVersionUID = -1149202795362173218L;

    @Override
    public void process(ArchivePlace place, Emitter<CheckResult> emitter) {
        for (ArchiveCheck check : ArchiveChecksFactory.getChecks()) {
            CheckResult checkResult = check.check(place);
            if (checkResult != null) {
                emitter.emit(checkResult);
            }
        }
    }
}
