package com.adaptive.solutions;

import static com.adaptive.solutions.Problem2.doubleEveryChar;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem2Test {

    @Test
    public void doubleEveryCharInNullStringReturnsNullString() {
        assertEquals(null, doubleEveryChar(null));
    }

    @Test
    public void doubleEveryCharInEmptyStringReturnsEmptyString() {
        assertEquals("", doubleEveryChar(""));
    }

    @Test
    public void doubleEveryCharInStringContainingOnlySpaces() {
        assertEquals("  ", doubleEveryChar(" "));
        assertEquals("    ", doubleEveryChar("  "));
    }

    @Test
    public void doubleEveryCharInStringContainingSpecialCharacters() {
        assertEquals("..", doubleEveryChar("."));
        assertEquals("$$##", doubleEveryChar("$#"));
        assertEquals("$$00", doubleEveryChar("$0"));
        assertEquals("aa$$", doubleEveryChar("a$"));
    }

    @Test
    public void doubleEveryCharInString() {
        assertEquals("aa", doubleEveryChar("a"));
        assertEquals("xxyyzz", doubleEveryChar("xyz"));
        assertEquals("xxyyzzxxyyzz", doubleEveryChar("xyzxyz"));
    }
}
