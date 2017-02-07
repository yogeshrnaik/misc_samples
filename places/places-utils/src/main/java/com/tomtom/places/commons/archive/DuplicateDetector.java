package com.tomtom.places.commons.archive;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Sets;
import com.tomtom.places.commons.avro.AvroFileReader;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class DuplicateDetector {

    public static void main(String[] args) throws IOException {
        AvroFileReader<ArchivePlace> places =
            new AvroFileReader<ArchivePlace>(
                "E:/Places/documents/GAPFM/Tickets/USA+MI-NullPointer/tmp");
        Set<String> unique = Sets.newHashSet();

        int counter = 0;

        for (ArchivePlace place : places) {
            counter++;
            String externalId = place.getPois().iterator().next().getExternalIdentifier().toString();
            if (!unique.add(externalId)) {
                System.out.println("*************** Duplicate POI: " + externalId);
            }

            if (counter % 1000 == 0) {
                System.out.println("processed " + counter);
            }
        }

        System.out.println("Total count: " + counter);

        IOUtils.closeQuietly(places);
    }
}
