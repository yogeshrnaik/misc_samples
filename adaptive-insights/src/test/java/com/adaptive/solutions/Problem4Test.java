package com.adaptive.solutions;

import static com.adaptive.solutions.Problem4.insertAsterik;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class Problem4Test {

    @Test
    public void insertAsterikInNullOrEmptryStringReturnsSameString() {
        assertEquals(null, insertAsterik(null));
        assertEquals("", insertAsterik(""));
    }

    @Test
    public void insertAsterikInSingleCharStringReturnsSameString() {
        assertEquals(" ", insertAsterik(" "));
        assertEquals("a", insertAsterik("a"));
        assertEquals("*", insertAsterik("*"));
    }

    @Test
    public void insertAsterikInString() {
        assertEquals(" * ", insertAsterik("  "));
        assertEquals("***", insertAsterik("**"));
        assertEquals("a*b", insertAsterik("ab"));
        assertEquals("a*b*c", insertAsterik("abc"));
        assertEquals("a*b*c*d", insertAsterik("abcd"));
    }
}
