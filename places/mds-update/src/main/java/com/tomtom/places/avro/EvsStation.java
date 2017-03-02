package com.tomtom.places.avro;

import com.tomtom.cpu.api.features.Attribute;

public class EvsStation {

    private final EvsSpotKey key;

    private int spotCount;

    private final Attribute<?> stationAttr;

    private final Attribute<?> spotAttr;

    public EvsStation(EvsSpotKey key, Attribute<?> stationAttr, Attribute<?> spotAttr) {
        this.key = key;
        spotCount = 1;
        this.stationAttr = stationAttr;
        this.spotAttr = spotAttr;
    }

    @Override
    public String toString() {
        return "EvsStation [spotCount=" + spotCount + ", stationAttr=" + stationAttr + "]";
    }

    public EvsSpotKey getKey() {
        return key;
    }

    public int getSpotCount() {
        return spotCount;
    }

    public void incrementSpotCount() {
        spotCount++;
    }

    public Attribute<?> getStationAttr() {
        return stationAttr;
    }

    public Attribute<?> getSpotAttr() {
        return spotAttr;
    }

}
