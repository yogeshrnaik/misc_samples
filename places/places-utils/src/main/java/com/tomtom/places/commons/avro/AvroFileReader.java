package com.tomtom.places.commons.avro;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.mapred.FsInput;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;

/**
 * Class to read any type of AVRO records from a file or a directory and from any file system. If the specified path is a path to a file,
 * only this file is read. If the specified path is a directory, all the AVRO files in this directory are read. If no file system is
 * specified, the local file system is used.
 * <p>
 * This class itself can be used to iterato, since it implements {@link Iterable}. After using the object, it has to be closed with the
 * {@link #close()} method to avoid leaking resources. </code>
 *
 * @author vanboxst
 * @param <T> AVRO record type
 */
public class AvroFileReader<T extends SpecificRecordBase> implements Iterable<T>, Closeable {

    private final String path;
    private final HdfsTools fileSystem;

    private ClosableIterable<T> iterable;
    private boolean iteratorConsumed;

    public AvroFileReader(String path) throws IOException {
        this(path, HdfsTools.forLocalFileSystem());
    }

    public AvroFileReader(String path, HdfsTools fileSystem) throws IOException {
        this.path = path;
        this.fileSystem = fileSystem;

        this.iterable = avroFilesIterable();
        this.iteratorConsumed = false;
    }

    @Override
    public void close() throws IOException {
        iterable.close();
    }

    @Override
    public Iterator<T> iterator() {
        if (iteratorConsumed) {
            throw new IllegalStateException("The iterator can be fetched only once.");
        }
        iteratorConsumed = true;
        return iterable.iterator();
    }

    private ClosableIterable<T> avroFilesIterable() throws IOException {

        if (!fileSystem.exists(path)) {
            throw new IOException("Could not find path " + path);
        }

        final List<ClosableIterable<T>> fileIterables = Lists.newArrayList();
        if (fileSystem.isFile(path)) {
            fileIterables.add(avroFileIterable(path));
        } else {
            List<String> avroFiles = fileSystem.listFiles(path, ".*\\.avro");
            for (String avroFile : avroFiles) {
                fileIterables.add(avroFileIterable(path + "/" + avroFile));
            }
        }

        return new ClosableIterable<T>() {

            @Override
            public void close() throws IOException {
                for (ClosableIterable<T> closableIterable : fileIterables) {
                    IOUtils.closeQuietly(closableIterable);
                }
            }

            @Override
            public Iterator<T> iterator() {
                return Iterables.concat(fileIterables).iterator();
            }
        };
    }

    private ClosableIterable<T> avroFileIterable(String avroFilePath) throws IOException {

        SeekableInput input = new FsInput(new Path(avroFilePath), fileSystem.getConfiguration());
        DatumReader<T> datumReader = new SpecificDatumReader<T>();
        final DataFileReader<T> reader = new DataFileReader<T>(input, datumReader);
        return new ClosableIterable<T>() {

            @Override
            public void close() throws IOException {
                reader.close();
            }

            @Override
            public Iterator<T> iterator() {
                return reader.iterator();
            }

        };
    }

    private static abstract class ClosableIterable<S> implements Iterable<S>, Closeable {

        @Override
        public abstract void close() throws IOException;

        @Override
        public abstract Iterator<S> iterator();

    }
}
