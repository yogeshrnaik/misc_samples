package com.tomtom.places.commons.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.tomtom.places.commons.avro.AvroFileReader;
import com.tomtom.places.commons.avro.AvroFileWriter;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class UniqueIteratorMain {

    public static void main(String[] args) throws IOException {
        // uniqueIntegerIterator();
        // uniqueStringIterator();
        uniqueArchivePlaceIterator();
    }

    private static void uniqueArchivePlaceIterator() throws IOException {
        AvroFileReader<ArchivePlace> reader = new AvroFileReader<ArchivePlace>("E:/TEMP/GAPFM/LIE/tmp/completed");
        UniqueIterator<ArchivePlace> uItr = new UniqueIterator<ArchivePlace>(reader) {

            @Override
            public String getIdentifier(ArchivePlace t) {
                return t.getPois().iterator().next().getExternalIdentifier().toString();
            }
        };

        write(uItr, "E:/TEMP/GAPFM/LIE/tmp/archive-places.avro");
        IOUtils.closeQuietly(reader);
    }

    private static void write(UniqueIterator<ArchivePlace> uItr, String outputFilePath) throws IOException {
        AvroFileWriter<ArchivePlace> writer = new AvroFileWriter<ArchivePlace>(outputFilePath, ArchivePlace.SCHEMA$, ArchivePlace.class);
        int counter = 0;
        while (uItr.hasNext()) {
            counter++;
            writer.write(uItr.next());
        }
        System.out.println("Total Unique Archive Places = " + counter);
        IOUtils.closeQuietly(writer);
    }

    private static void uniqueStringIterator() {
        UniqueIterator<String> uItr = new UniqueIterator<String>(Lists.newArrayList("A", "a", "A").iterator()) {

            @Override
            public String getIdentifier(String t) {
                return t.toLowerCase();
            }
        };
        print(uItr);
    }

    private static void uniqueIntegerIterator() {
        List<Integer> input = Lists.newArrayList(1, 1, 1, 2, 4, 4, 5, 5, -10, 3, 3);
        remove(input);
        System.out.println(input);
        System.out.println("*********************************");

        input = Lists.newArrayList(1, 1, 1, 2, 4, 4, 5, 5, -10, 3, 3);
        print(input);
    }

    private static <T> void remove(List<T> input) {
        UniqueIterator<T> uniqueItr = new UniqueIterator<T>(input.iterator());
        while (uniqueItr.hasNext()) {
            uniqueItr.remove();
        }
    }

    private static <T> void print(Iterator<T> itr) {
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
    }

    private static <T> void print(List<T> input) {
        UniqueIterator<T> uniqueItr = new UniqueIterator<T>(input.iterator());
        while (uniqueItr.hasNext()) {
            System.out.println(uniqueItr.next());
        }
    }
}
