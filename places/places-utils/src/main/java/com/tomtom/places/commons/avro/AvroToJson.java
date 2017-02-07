package com.tomtom.places.commons.avro;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.avro.specific.SpecificRecordBase;

import com.tomtom.places.unicorn.domain.util.SerializationUtil;
import com.tomtom.places.unicorn.pipelineutil.AvroFileReader;

public class AvroToJson {

    public static <T extends SpecificRecordBase> void avroToJson(String avroPath, String jsonPath) throws IOException {
        try (AvroFileReader<T> reader = new AvroFileReader<T>(avroPath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonPath))) {
            for (T t : reader) {
                String json = SerializationUtil.convertToJson(t);
                writer.write(json);
                writer.write("\r\n");
            }
            writer.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        avroToJson("E:/Places/code/ppp-cycletime/archive-delta-clusterer/src/test/resources/LUX/DeltaClusterer/input_delta",
            "E:/Places/code/ppp-cycletime/archive-delta-clusterer/src/test/resources/LUX/DeltaClusterer/input_delta/input_delta.json");
    }
}
