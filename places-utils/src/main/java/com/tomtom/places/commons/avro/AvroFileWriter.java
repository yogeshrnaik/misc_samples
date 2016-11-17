package com.tomtom.places.commons.avro;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.IOUtils;

import com.tomtom.places.unicorn.pipelineutil.HdfsTools;

public class AvroFileWriter<T> implements Closeable {

    private DatumWriter<T> datumWriter;
    private DataFileWriter<T> dataFileWriter;
    private FileOutputStream outputStream;
    private final HdfsTools fileSystem;

    public AvroFileWriter(String outputFilePath, Schema schema, Class<T> clazz) throws IOException {
        this(outputFilePath, schema, clazz, HdfsTools.forLocalFileSystem());
    }

    public AvroFileWriter(String outputFilePath, Schema schema, Class<T> clazz, HdfsTools fileSystem) throws IOException {
        if (!fileSystem.exists(outputFilePath)) {
            fileSystem.createFile(outputFilePath);
        }

        datumWriter = new SpecificDatumWriter<T>(clazz);
        dataFileWriter = new DataFileWriter<T>(datumWriter);
        outputStream = new FileOutputStream(outputFilePath, false);
        dataFileWriter.create(schema, outputStream);

        this.fileSystem = fileSystem;
    }

    public void write(T data) throws IOException {
        dataFileWriter.append(data);
    }

    public void flush() throws IOException {
        if (outputStream != null) {
            outputStream.flush();
        }

        if (dataFileWriter != null) {
            dataFileWriter.flush();
        }
    }

    @Override
    public void close() throws IOException {
        flush();

        IOUtils.closeQuietly(outputStream);
        IOUtils.closeQuietly(dataFileWriter);
    }

}