package com.tomtom.places.trace.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;

import com.cloudera.crunch.PCollection;

public class Utils {

    public static <E> List<E> materialize(PCollection<E> pcollection) {
        List<E> result = new ArrayList<E>();
        Iterator<E> iterator = pcollection.materialize().iterator();

        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public static void print(PCollection<? extends SpecificRecordBase> pcoll) {
        List<? extends SpecificRecordBase> list = Utils.materialize(pcoll);
        for (SpecificRecordBase r : list) {
            System.out.println(r);
        }
    }
}
