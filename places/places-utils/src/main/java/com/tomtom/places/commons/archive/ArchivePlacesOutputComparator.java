package com.tomtom.places.commons.archive;

import static com.tomtom.places.commons.avro.CrunchUtils.materialize;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.hadoop.fs.Path;

import com.cloudera.crunch.impl.mem.MemPipeline;
import com.cloudera.crunch.io.avro.AvroFileSource;
import com.cloudera.crunch.types.avro.Avros;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;

public class ArchivePlacesOutputComparator {

    private static final class ArchivePlaceComparator implements Comparator<ArchivePlace> {

        @Override
        public int compare(ArchivePlace ap1, ArchivePlace ap2) {
            return ap1.getPois().iterator().next().getExternalIdentifier().toString()
                .compareTo(ap2.getPois().iterator().next().getExternalIdentifier().toString());
        }
    }

    public static void main(String[] args) {
        List<ArchivePlace> fullRun =
            materialize(MemPipeline.getInstance().read(
                new AvroFileSource<ArchivePlace>(new Path("E:/TEMP/GAPFM/LIE-Full-Run-Reduce=200000/archive-places"), Avros
                    .records(ArchivePlace.class))));
        List<ArchivePlace> interrupedRun = materialize(MemPipeline.getInstance().read(
            new AvroFileSource<ArchivePlace>(new Path("E:/TEMP/GAPFM/LIE/archive-places"), Avros.records(ArchivePlace.class))));

        Collections.sort(fullRun, new ArchivePlaceComparator());
        Collections.sort(interrupedRun, new ArchivePlaceComparator());

        List<String> externalIdsFull = Lists.newArrayList(Iterables.transform(fullRun, new Function<ArchivePlace, String>() {

            @Override
            public String apply(ArchivePlace input) {
                return input.getPois().iterator().next().getExternalIdentifier().toString();
            }
        }));

        List<String> externalIdsInterruped = Lists.newArrayList(Iterables.transform(interrupedRun, new Function<ArchivePlace, String>() {

            @Override
            public String apply(ArchivePlace input) {
                return input.getPois().iterator().next().getExternalIdentifier().toString();
            }
        }));

        printList(externalIdsFull);
        System.out.println("********************************");
        printList(externalIdsInterruped);
    }

    public static <E> void printList(List<E> list) {
        for (E e : list) {
            System.out.println(e);
        }
    }
}
