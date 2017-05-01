package com.adaptive.solutions;

import static com.adaptive.solutions.Problem2.doubleEveryCharWithLoop;
import static com.adaptive.solutions.Problem2.doubleEveryCharWithRegex;
import static com.adaptive.solutions.Problem2.doubleEveryCharWithStream;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem2Test {

    @Test
    public void doubleEveryCharInNullStringReturnsNullString() {
        // test for regex implementation
        assertEquals(null, doubleEveryCharWithRegex(null));

        // test for loop implementation
        assertEquals(null, doubleEveryCharWithLoop(null));

        // test for stream implementation
        assertEquals(null, doubleEveryCharWithStream(null));
    }

    @Test
    public void doubleEveryCharInEmptyStringReturnsEmptyString() {
        // test for regex implementation
        assertEquals("", doubleEveryCharWithRegex(""));

        // test for loop implementation
        assertEquals("", doubleEveryCharWithLoop(""));

        // test for stream implementation
        assertEquals("", doubleEveryCharWithStream(""));
    }

    @Test
    public void doubleEveryCharInStringContainingOnlySpaces() {
        // test for regex implementation
        assertEquals("  ", doubleEveryCharWithRegex(" "));
        assertEquals("    ", doubleEveryCharWithRegex("  "));

        // test for loop implementation
        assertEquals("  ", doubleEveryCharWithLoop(" "));
        assertEquals("    ", doubleEveryCharWithLoop("  "));

        // test for stream implementation
        assertEquals("  ", doubleEveryCharWithStream(" "));
        assertEquals("    ", doubleEveryCharWithStream("  "));
    }

    @Test
    public void doubleEveryCharInStringContainingSpecialCharacters() {
        // test for regex implementation
        assertEquals("..", doubleEveryCharWithRegex("."));
        assertEquals("$$##", doubleEveryCharWithRegex("$#"));
        assertEquals("$$00", doubleEveryCharWithRegex("$0"));
        assertEquals("aa$$", doubleEveryCharWithRegex("a$"));

        // test for loop implementation
        assertEquals("..", doubleEveryCharWithLoop("."));
        assertEquals("$$##", doubleEveryCharWithLoop("$#"));
        assertEquals("$$00", doubleEveryCharWithLoop("$0"));
        assertEquals("aa$$", doubleEveryCharWithLoop("a$"));

        // test for stream implementation
        assertEquals("..", doubleEveryCharWithStream("."));
        assertEquals("$$##", doubleEveryCharWithStream("$#"));
        assertEquals("$$00", doubleEveryCharWithStream("$0"));
        assertEquals("aa$$", doubleEveryCharWithStream("a$"));
    }

    @Test
    public void doubleEveryCharInString() {
        // test for regex implementation
        assertEquals("aa", doubleEveryCharWithRegex("a"));
        assertEquals("xxyyzz", doubleEveryCharWithRegex("xyz"));
        assertEquals("xxyyzzxxyyzz", doubleEveryCharWithRegex("xyzxyz"));

        // test for loop implementation
        assertEquals("aa", doubleEveryCharWithLoop("a"));
        assertEquals("xxyyzz", doubleEveryCharWithLoop("xyz"));
        assertEquals("xxyyzzxxyyzz", doubleEveryCharWithLoop("xyzxyz"));

        // test for stream implementation
        assertEquals("aa", doubleEveryCharWithStream("a"));
        assertEquals("xxyyzz", doubleEveryCharWithStream("xyz"));
        assertEquals("xxyyzzxxyyzz", doubleEveryCharWithStream("xyzxyz"));
    }
}
