package com.tomtom.places.misc.adminarea;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class Locality implements Serializable {

    private static final String COUNTRY_STATE_SEPARATOR = "+";
    private static final String COUNTRY_STATE_SEPARATOR_REGEX = "\\" + COUNTRY_STATE_SEPARATOR;

    private static final long serialVersionUID = -6533177254253620463L;

    private final String country;
    private final String state;

    public Locality(String loc) {
        String[] countryAndState = getCountryAndState(loc);
        country = countryAndState[0];
        state = countryAndState[1];
    }

    private static String[] getCountryAndState(String localityString) {
        if (localityString.contains(COUNTRY_STATE_SEPARATOR)) {
            return localityString.split(COUNTRY_STATE_SEPARATOR_REGEX); // escape the + symbol as it has special meaning in regex
        }
        return new String[] {localityString, ""};
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(state)) {
            return country + COUNTRY_STATE_SEPARATOR + state;
        }
        return country;
    }

}
