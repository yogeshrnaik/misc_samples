package com.adaptive.solutions;

import static com.adaptive.solutions.Problem1.getClosestTo;
import static com.adaptive.solutions.Problem1.getClosestTo1000;
import static com.adaptive.solutions.Problem1.getClosestToMaxInt;
import static com.adaptive.solutions.Problem1.getClosestToMinInt;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem1Test {

    @Test
    public void givenBothNumbersSameReturnsSameNumber() {
        assertEquals(10, getClosestTo1000(10, 10));
        assertEquals(1000, getClosestTo1000(1000, 1000));

        assertEquals(-10, getClosestTo1000(-10, -10));
    }

    @Test
    public void givenTwoNumbersReturnsClosedNumberTo1000() {
        assertEquals(100, getClosestTo1000(100, 10));
        assertEquals(100, getClosestTo1000(10, 100));

        assertEquals(0, getClosestTo1000(0, -1));
        assertEquals(10, getClosestTo1000(10, -10));
        assertEquals(-10, getClosestTo1000(-11, -10));

        assertEquals(1000, getClosestTo1000(1000, -1000));
        assertEquals(-999, getClosestTo1000(-1000, -999));
    }

    @Test
    public void whenBothNumbersAreCloseTo1000Returns1stNumber() {
        assertEquals(990, getClosestTo1000(990, 1010));
        assertEquals(1010, getClosestTo1000(1010, 990));
    }

    @Test
    public void givenTwoNumbersReturnsClosedNumberToZero() {
        assertEquals(10, getClosestTo(100, 10, 0));
        assertEquals(10, getClosestTo(10, 100, 0));

        assertEquals(0, getClosestTo(0, -1, 0));
        assertEquals(10, getClosestTo(10, -10, 0));
        assertEquals(-10, getClosestTo(-11, -10, 0));

        assertEquals(1000, getClosestTo(1000, -1000, 0));
        assertEquals(-999, getClosestTo(-1000, -999, 0));
    }

    @Test
    public void whenInputNumbersAreEitherMinAndMaxIntThenReturnsClosedNumberTo1000() {
        assertEquals(Integer.MAX_VALUE, getClosestTo1000(Integer.MAX_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestTo1000(Integer.MIN_VALUE, Integer.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, getClosestTo1000(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestTo1000(Integer.MAX_VALUE, Integer.MAX_VALUE));

        assertEquals(0, getClosestTo1000(0, Integer.MIN_VALUE));
        assertEquals(0, getClosestTo1000(0, Integer.MAX_VALUE));

        assertEquals(-10, getClosestTo1000(-10, Integer.MIN_VALUE));
        assertEquals(-10, getClosestTo1000(-10, Integer.MAX_VALUE));
    }

    @Test
    public void returnClosedNumberToMinIntValue() {
        assertEquals(Integer.MIN_VALUE, getClosestToMinInt(Integer.MAX_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MIN_VALUE, getClosestToMinInt(Integer.MIN_VALUE, Integer.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, getClosestToMinInt(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestToMinInt(Integer.MAX_VALUE, Integer.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, getClosestToMinInt(0, Integer.MIN_VALUE));
        assertEquals(0, getClosestToMinInt(0, Integer.MAX_VALUE));

        assertEquals(-10, getClosestToMinInt(-10, Integer.MAX_VALUE));
        assertEquals(Integer.MIN_VALUE, getClosestToMinInt(-10, Integer.MIN_VALUE));
    }

    @Test
    public void returnClosedNumberToMaxIntValue() {
        assertEquals(Integer.MAX_VALUE, getClosestToMaxInt(Integer.MAX_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestToMaxInt(Integer.MIN_VALUE, Integer.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, getClosestToMaxInt(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestToMaxInt(Integer.MAX_VALUE, Integer.MAX_VALUE));

        assertEquals(Integer.MAX_VALUE, getClosestToMaxInt(0, Integer.MAX_VALUE));
        assertEquals(0, getClosestToMaxInt(0, Integer.MIN_VALUE));

        assertEquals(-10, getClosestToMaxInt(-10, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestToMaxInt(-10, Integer.MAX_VALUE));
    }
}
