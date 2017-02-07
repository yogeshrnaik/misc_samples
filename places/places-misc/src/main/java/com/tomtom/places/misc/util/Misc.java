package com.tomtom.places.misc.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.base.Strings;
import com.tomtom.places.misc.avro.AvroFileWriter;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.pipelineutil.AvroFileReader;

public class Misc {

    public static void main(String[] args) throws IOException {
        Collection<File> files =
            FileUtils.listFiles(new File("E:/Places/code/ppp-cycletime/generate-archive-places-from-mds/src/test/resources/BEL"),
                new String[] {"avro"}, false);

        int i = 0;
        for (File f : files) {
            i++;
            AvroFileReader<ArchivePlace> places = new AvroFileReader<ArchivePlace>(f.getAbsolutePath());
            AvroFileWriter<ArchivePlace> writer =
                new AvroFileWriter<ArchivePlace>(
                    "E:/Places/code/ppp-cycletime/generate-archive-places-from-mds/src/test/resources/BEL/updated/archive-places-"
                        + Strings.padStart(Long.toString(i), 4, '0') + ".avro", ArchivePlace.SCHEMA$, ArchivePlace.class);
            for (ArchivePlace p : places) {
                p.getPois().iterator().next().setCppId(UUID.randomUUID().toString());
                writer.write(p);

            }
            IOUtils.closeQuietly(places);
            IOUtils.closeQuietly(writer);
        }
    }
}
