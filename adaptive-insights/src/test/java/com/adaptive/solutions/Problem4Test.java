package com.adaptive.solutions;

import static com.adaptive.solutions.Problem4.insertAsterikRecursive;
import static com.adaptive.solutions.Problem4.insertAsterikWithLoop;
import static com.adaptive.solutions.Problem4.insertAsterikWithStream;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class Problem4Test {

    @Test
    public void insertAsterikInNullOrEmptryStringReturnsSameString() {
        // test for recursive implementation
        assertEquals(null, insertAsterikRecursive(null));
        assertEquals("", insertAsterikRecursive(""));

        // test for implementation with loop
        assertEquals(null, insertAsterikWithLoop(null));
        assertEquals("", insertAsterikWithLoop(""));

        // test for implementation with stream
        assertEquals(null, insertAsterikWithStream(null));
        assertEquals("", insertAsterikWithStream(""));
    }

    @Test
    public void insertAsterikInSingleCharStringReturnsSameString() {
        // test for recursive implementation
        assertEquals(" ", insertAsterikRecursive(" "));
        assertEquals("a", insertAsterikRecursive("a"));
        assertEquals("*", insertAsterikRecursive("*"));

        // test for implementation with loop
        assertEquals(" ", insertAsterikWithLoop(" "));
        assertEquals("a", insertAsterikWithLoop("a"));
        assertEquals("*", insertAsterikWithLoop("*"));

        // test for implementation with stream
        assertEquals(" ", insertAsterikWithStream(" "));
        assertEquals("a", insertAsterikWithStream("a"));
        assertEquals("*", insertAsterikWithStream("*"));
    }

    @Test
    public void insertAsterikInString() {
        // test for recursive implementation
        assertEquals(" * ", insertAsterikRecursive("  "));
        assertEquals("***", insertAsterikRecursive("**"));
        assertEquals("a*b", insertAsterikRecursive("ab"));
        assertEquals("a*b*c", insertAsterikRecursive("abc"));
        assertEquals("a*b*c*d", insertAsterikRecursive("abcd"));

        // test for implementation with loop
        assertEquals(" * ", insertAsterikWithLoop("  "));
        assertEquals("***", insertAsterikWithLoop("**"));
        assertEquals("a*b", insertAsterikWithLoop("ab"));
        assertEquals("a*b*c", insertAsterikWithLoop("abc"));
        assertEquals("a*b*c*d", insertAsterikWithLoop("abcd"));

        // test for implementation with stream
        assertEquals(" * ", insertAsterikWithStream("  "));
        assertEquals("***", insertAsterikWithStream("**"));
        assertEquals("a*b", insertAsterikWithStream("ab"));
        assertEquals("a*b*c", insertAsterikWithStream("abc"));
        assertEquals("a*b*c*d", insertAsterikWithStream("abcd"));
    }
}
