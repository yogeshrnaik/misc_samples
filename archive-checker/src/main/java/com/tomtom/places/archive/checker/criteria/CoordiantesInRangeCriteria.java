package com.tomtom.places.archive.checker.criteria;

import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.archive.POI;

public class CoordiantesInRangeCriteria extends ArchiveCriteria {

    private static final long serialVersionUID = 7941291047177401495L;

    private final double longitudeOfPoi;
    private final double latitudeOfPoi;
    private final double range;

    public CoordiantesInRangeCriteria(double longitudeOfPoi, double latitudeOfPoi, double range) {
        this.longitudeOfPoi = longitudeOfPoi;
        this.latitudeOfPoi = latitudeOfPoi;
        this.range = range;
    }

    @Override
    public boolean isCriteriaMatch(ArchivePlace place) {
        return coordiantesInRange(getPOI(place));
    }

    private boolean coordiantesInRange(POI poi) {
        return Math.abs(Double.parseDouble(poi.getLongitudeOfPoi().toString()) - longitudeOfPoi) < range
            && Math.abs(Double.parseDouble(poi.getLatitudeOfPoi().toString()) - latitudeOfPoi) < range;
    }

    @Override
    public String toString() {
        return "CoordiantesInRangeCriteria [longitudeOfPoi=" + longitudeOfPoi + ", latitudeOfPoi=" + latitudeOfPoi
            + ", range=" + range + "]";
    }
}
