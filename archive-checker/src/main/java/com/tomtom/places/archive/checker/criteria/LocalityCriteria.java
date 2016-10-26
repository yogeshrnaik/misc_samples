package com.tomtom.places.archive.checker.criteria;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class LocalityCriteria extends ArchiveCriteria {

    private static final long serialVersionUID = -3963269937041412970L;

    private final List<String> localities;

    public LocalityCriteria(List<String> locs) {
        localities = Lists.newArrayList();
        for (String loc : locs) {
            localities.add(new Locality(loc).toString().toUpperCase());
        }

    }

    @Override
    public boolean isCriteriaMatch(ArchivePlace place) {
        Locality loc = new Locality(place.getIso3Country(), place.getState());
        return localities.contains(loc.toString().toUpperCase());
    }

    @Override
    public String toString() {
        return "LocalityCriteria [localities=" + localities + "]";
    }

    private static final class Locality {

        private static final String COUNTRY_STATE_SEPARATOR = "+";
        private static final String COUNTRY_STATE_SEPARATOR_REGEX = "\\" + COUNTRY_STATE_SEPARATOR;

        private final String country;
        private final String state;

        public Locality(String loc) {
            String[] countryAndState = getCountryAndState(loc);
            country = countryAndState[0];
            state = countryAndState[1];
        }

        public Locality(CharSequence country, CharSequence state) {
            this.country = country != null ? country.toString() : "";
            this.state = state != null ? state.toString() : "";
        }

        private static String[] getCountryAndState(String localityString) {
            if (localityString.contains(COUNTRY_STATE_SEPARATOR)) {
                return localityString.split(COUNTRY_STATE_SEPARATOR_REGEX); // escape the + symbol as it has special meaning in regex
            }
            return new String[] {localityString, ""};
        }

        @Override
        public String toString() {
            if (StringUtils.isNotBlank(state)) {
                return country + COUNTRY_STATE_SEPARATOR + state;
            }
            return country;
        }

    }

}
