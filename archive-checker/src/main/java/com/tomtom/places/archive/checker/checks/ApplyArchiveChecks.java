package com.tomtom.places.archive.checker.checks;

import com.cloudera.crunch.DoFn;
import com.cloudera.crunch.Emitter;
import com.tomtom.places.archive.checker.result.CheckResult;
import com.tomtom.places.archive.checker.util.ArchivePlaceCounter;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class ApplyArchiveChecks extends DoFn<ArchivePlace, CheckResult> {

    private static final long serialVersionUID = -1149202795362173218L;

    @Override
    public void process(ArchivePlace place, Emitter<CheckResult> emitter) {
        for (ArchiveCheck check : ArchiveChecksFactory.getChecks()) {
            if (check.isApplicable(place)) {
                increment(ArchivePlaceCounter.Applicable);
                getCounter(check.getCheckId(), "Applicable").increment(1);

                CheckResult checkResult = check.check(place);
                if (checkResult != null) {
                    increment(ArchivePlaceCounter.GotResult);
                    getCounter(check.getCheckId(), "GotResult").increment(1);
                    emitter.emit(checkResult);
                }
            }
        }
    }
}
