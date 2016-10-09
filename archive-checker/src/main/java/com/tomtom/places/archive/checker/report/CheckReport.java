package com.tomtom.places.archive.checker.report;

import java.io.Serializable;

public abstract class CheckReport implements Serializable {

    private static final long serialVersionUID = -821292876090056232L;

    private final String checkId;

    protected CheckReport(String checkId) {
        this.checkId = checkId;
    }

}
