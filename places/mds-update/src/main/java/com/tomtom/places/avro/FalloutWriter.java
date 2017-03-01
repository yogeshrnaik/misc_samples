package com.tomtom.places.avro;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

public class FalloutWriter {

    private final File falloutFile;

    public FalloutWriter(String falloutFile) {
        this.falloutFile = new File(falloutFile);
    }

    public synchronized void writeFallout(String line) throws IOException {
        String data = line + "\n";
        FileUtils.writeStringToFile(falloutFile, data, Charset.forName("UTF-8"), true);
    }
}
