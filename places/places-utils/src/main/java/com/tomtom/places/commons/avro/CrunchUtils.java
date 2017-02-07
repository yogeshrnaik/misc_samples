package com.tomtom.places.commons.avro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cloudera.crunch.PCollection;

public class CrunchUtils {

    public static <E> List<E> materialize(PCollection<E> pcollection) {
        List<E> result = new ArrayList<E>();
        Iterator<E> iterator = pcollection.materialize().iterator();

        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
}
