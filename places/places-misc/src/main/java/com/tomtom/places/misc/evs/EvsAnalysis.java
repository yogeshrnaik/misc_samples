package com.tomtom.places.misc.evs;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.productized.archive.ArchivePlaces;
import com.tomtom.places.unicorn.pipelineutil.AvroFileReader;

public class EvsAnalysis {

    public static void main(String[] args) throws Exception {
    }

    private static void getCppIDs() throws IOException {
        AvroFileReader<ArchivePlace> places = new AvroFileReader<ArchivePlace>("E:/Places/documents/EVS/NLD/archive-places");
        for (ArchivePlace place : places) {
            System.out.println(ArchivePlaces.getCppId(place));// + "\t" + ArchivePlaces.getExternalId(place));
        }

        IOUtils.closeQuietly(places);
    }
}
