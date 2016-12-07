package com.tomtom.places.commons.archive.fallout;

import java.io.IOException;

import com.tomtom.places.commons.avro.AvroFileReader;
import com.tomtom.places.commons.avro.AvroFileWriter;
import com.tomtom.places.unicorn.domain.avro.archive.ArchiveFallout;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlaceDiff;
import com.tomtom.places.unicorn.domain.avro.archive.POI;
import com.tomtom.places.unicorn.domain.avro.archive.RelatedArchivePlaceDiff;

public class FalloutUtil {

    public static void main(String[] args) throws IOException {
        AvroFileReader<ArchiveFallout> fallouts =
            new AvroFileReader<ArchiveFallout>("E:/Places/documents/ArchiveToMDS/ROU_Issue/fallout/fallout-completed-1479747722447.avro");

        AvroFileWriter<ArchiveFallout> writer =
            new AvroFileWriter<ArchiveFallout>("E:/Places/documents/ArchiveToMDS/ROU_Issue/fallout/ROU-manipulated-fallout.avro",
                ArchiveFallout.SCHEMA$, ArchiveFallout.class);

        for (ArchiveFallout f : fallouts) {
            RelatedArchivePlaceDiff relatedDiff = f.getFallout();

            for (ArchivePlaceDiff diff : relatedDiff.getArchivePlaceDiffs()) {
                if (diff.getBefore() == null && diff.getAfter() != null) {
                    POI poi = diff.getAfter().getPois().iterator().next();
                    if (poi.getCppId() != null) {
                        System.out.println("Removed CPP ID: " + poi.getCppId());
                        poi.setCppId(null);
                    }
                }
            }

            writer.write(f);
        }

        writer.flush();
        writer.close();
        fallouts.close();
    }
}
