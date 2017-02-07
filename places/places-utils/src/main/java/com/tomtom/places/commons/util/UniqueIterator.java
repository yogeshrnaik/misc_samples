package com.tomtom.places.commons.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.Sets;

public class UniqueIterator<T> implements Iterator<T> {

    private Iterator<T> itr;

    private Set<String> unique = Sets.newHashSet();

    private T nextUniqueItem;

    public UniqueIterator(Iterator<T> itr) {
        this.itr = itr;
    }

    public UniqueIterator(Iterable<T> itr) {
        this.itr = itr.iterator();
    }

    @Override
    public boolean hasNext() {
        if (nextUniqueItem != null) {
            return true;
        }

        while (itr.hasNext()) {
            T next = itr.next();
            if (!unique.add(getIdentifier(next))) {
                // duplicate item found
                continue;
            } else {
                nextUniqueItem = next;
                return true;
            }
        }

        return false;
    }

    @Override
    public T next() {
        if (hasNext()) {
            T result = nextUniqueItem;
            nextUniqueItem = null;
            return result;
        }
        throw new NoSuchElementException("There are no more elements left in the UniqueIterable.");
    }

    @Override
    public void remove() {
        if (nextUniqueItem == null) {
            while (itr.hasNext()) {
                T next = itr.next();
                if (!unique.add(getIdentifier(next))) {
                    // duplicate item found
                    continue;
                } else {
                    itr.remove();
                }
            }
        } else {
            itr.remove();
            nextUniqueItem = null;
        }
    }

    public String getIdentifier(T t) {
        return t.toString();
    }
}
