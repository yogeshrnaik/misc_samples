package com.adaptive.solutions;

import static com.adaptive.solutions.Problem4.insertAsterikRecursive;
import static com.adaptive.solutions.Problem4.insertAsterikWithLoop;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class Problem4Test {

    @Test
    public void insertAsterikInNullOrEmptryStringReturnsSameString() {
        assertEquals(null, insertAsterikRecursive(null));
        assertEquals("", insertAsterikRecursive(""));

        assertEquals(null, insertAsterikWithLoop(null));
        assertEquals("", insertAsterikWithLoop(""));
    }

    @Test
    public void insertAsterikInSingleCharStringReturnsSameString() {
        assertEquals(" ", insertAsterikRecursive(" "));
        assertEquals("a", insertAsterikRecursive("a"));
        assertEquals("*", insertAsterikRecursive("*"));

        assertEquals(" ", insertAsterikWithLoop(" "));
        assertEquals("a", insertAsterikWithLoop("a"));
        assertEquals("*", insertAsterikWithLoop("*"));
    }

    @Test
    public void insertAsterikInString() {
        assertEquals(" * ", insertAsterikRecursive("  "));
        assertEquals("***", insertAsterikRecursive("**"));
        assertEquals("a*b", insertAsterikRecursive("ab"));
        assertEquals("a*b*c", insertAsterikRecursive("abc"));
        assertEquals("a*b*c*d", insertAsterikRecursive("abcd"));

        assertEquals(" * ", insertAsterikWithLoop("  "));
        assertEquals("***", insertAsterikWithLoop("**"));
        assertEquals("a*b", insertAsterikWithLoop("ab"));
        assertEquals("a*b*c", insertAsterikWithLoop("abc"));
        assertEquals("a*b*c*d", insertAsterikWithLoop("abcd"));
    }
}
