package com.tomtom.places.commons.avro;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.io.FileUtils;

import com.tomtom.places.unicorn.domain.avro.archive.RelatedArchivePlaceDiff;

public class JsonToAvro {

    public static <T extends SpecificRecordBase> void jsonToAvro(String jsonFilePath, Schema schema, Class<T> clazz, String avroFilePath)
        throws IOException {
        try (AvroFileWriter<T> writer = new AvroFileWriter<T>(avroFilePath, schema, clazz)) {
            List<String> lines = FileUtils.readLines(new File(jsonFilePath));
            for (String json : lines) {
                T avroObj = convertToAvroObject(schema, json);
                writer.write(avroObj);
            }
            writer.flush();
        }
    }

    private static <T extends SpecificRecordBase> T convertToAvroObject(final Schema avroSchema, final String json) throws IOException {
        final JsonDecoder decoder = DecoderFactory.get().jsonDecoder(avroSchema, json);
        final SpecificDatumReader<T> reader = new SpecificDatumReader<T>(avroSchema);
        return reader.read(null, decoder);
    }

    public static void main(String[] args) throws IOException {
        jsonToAvro("E:/Places/code/ppp-cycletime/archive-delta-clusterer/src/test/resources/LUX/DeltaClusterer/input_delta/output.json",
            RelatedArchivePlaceDiff.SCHEMA$, RelatedArchivePlaceDiff.class,
            "E:/Places/code/ppp-cycletime/archive-delta-clusterer/src/test/resources/LUX/DeltaClusterer/input_delta/input_delta.avro");
    }
}
