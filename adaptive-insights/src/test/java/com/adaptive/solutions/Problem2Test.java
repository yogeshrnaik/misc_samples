package com.adaptive.solutions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem2Test {

    @Test
    public void doubleEveryCharInString() {
        assertEquals("", Problem2.doubleEveryChar(""));

        assertEquals("..", Problem2.doubleEveryChar("."));
        assertEquals("  ", Problem2.doubleEveryChar(" "));

        assertEquals("$$##", Problem2.doubleEveryChar("$#"));

        assertEquals("aa", Problem2.doubleEveryChar("a"));
        assertEquals("xxyyzz", Problem2.doubleEveryChar("xyz"));
        assertEquals("xxyyzzxxyyzz", Problem2.doubleEveryChar("xyzxyz"));
    }
}
