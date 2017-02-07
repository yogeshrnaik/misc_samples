package com.tomtom.places.archive.checker.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.IOUtils;

import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.util.SerializationUtil;
import com.tomtom.places.unicorn.pipelineutil.AvroFileReader;

public class ArchiveCheckerTestHelper {

    public static void main(String[] args) throws IOException {
        DatumWriter<ArchivePlace> datumWriter = new SpecificDatumWriter<ArchivePlace>(ArchivePlace.class);
        DataFileWriter<ArchivePlace> falloutDatafileWriter = new DataFileWriter<ArchivePlace>(datumWriter);
        FileOutputStream falloutOutputStream = new FileOutputStream("src/test/resources/archive-places/input.avro", true);

        falloutDatafileWriter.create(ArchivePlace.SCHEMA$, falloutOutputStream);

        List<ArchivePlace> places = SerializationUtil.loadFromJsons(ArchivePlace.SCHEMA$, "src/test/resources/archive-places/input.json");
        for (ArchivePlace place : places) {
            falloutDatafileWriter.append(place);
            falloutDatafileWriter.flush();
        }
        falloutDatafileWriter.close();
        falloutOutputStream.close();

    }

    private static void writeJsons() throws IOException {
        int fileCounter = 1;
        AvroFileReader<ArchivePlace> reader = new AvroFileReader<ArchivePlace>("src/test/resources/archive-places/input.avro");

        for (ArchivePlace place : reader) {
            SerializationUtil.saveToJson(place, "src/test/resources/archive-places/" + fileCounter++ + ".json");
        }

        IOUtils.closeQuietly(reader);
    }
}
