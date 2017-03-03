package com.tomtom.places.unicorn.mdsupdate.evs;

public class EvsSpotKey {

    private final String stationId;
    private final Integer chargingSpotServicesBitmask;
    private final Integer chargingFacilitiesBitmask;
    private final Integer receptacleType;

    public EvsSpotKey(String stationId, Integer chargingSpotServicesBitmask, Integer chargingFacilitiesBitmask, Integer receptacleType) {
        this.stationId = stationId;
        this.chargingSpotServicesBitmask = chargingSpotServicesBitmask;
        this.chargingFacilitiesBitmask = chargingFacilitiesBitmask;
        this.receptacleType = receptacleType;
    }

    @Override
    public String toString() {
        return "EvsSpotKey [stationId=" + stationId + ", chargingSpotServicesBitmask=" + chargingSpotServicesBitmask
            + ", chargingFacilitiesBitmask=" + chargingFacilitiesBitmask + ", receptacleType=" + receptacleType + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (chargingFacilitiesBitmask == null ? 0 : chargingFacilitiesBitmask.hashCode());
        result = prime * result + (chargingSpotServicesBitmask == null ? 0 : chargingSpotServicesBitmask.hashCode());
        result = prime * result + (receptacleType == null ? 0 : receptacleType.hashCode());
        result = prime * result + (stationId == null ? 0 : stationId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EvsSpotKey other = (EvsSpotKey)obj;
        if (chargingFacilitiesBitmask == null) {
            if (other.chargingFacilitiesBitmask != null) {
                return false;
            }
        } else if (!chargingFacilitiesBitmask.equals(other.chargingFacilitiesBitmask)) {
            return false;
        }
        if (chargingSpotServicesBitmask == null) {
            if (other.chargingSpotServicesBitmask != null) {
                return false;
            }
        } else if (!chargingSpotServicesBitmask.equals(other.chargingSpotServicesBitmask)) {
            return false;
        }
        if (receptacleType == null) {
            if (other.receptacleType != null) {
                return false;
            }
        } else if (!receptacleType.equals(other.receptacleType)) {
            return false;
        }
        if (stationId == null) {
            if (other.stationId != null) {
                return false;
            }
        } else if (!stationId.equals(other.stationId)) {
            return false;
        }
        return true;
    }

}
