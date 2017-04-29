package com.adaptive.solutions;

import static com.adaptive.solutions.Problem1.getClosestTo1000;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem1Test {

    @Test
    public void returnClosedNumberTo1000() {
        assertEquals(10, getClosestTo1000(10, 10));
        assertEquals(1000, getClosestTo1000(1000, 1000));

        assertEquals(100, getClosestTo1000(100, 10));
        assertEquals(100, getClosestTo1000(10, 100));

        assertEquals(0, getClosestTo1000(0, -1));
        assertEquals(10, getClosestTo1000(10, -10));
        assertEquals(-10, getClosestTo1000(-11, -10));

        assertEquals(1000, getClosestTo1000(1000, -1000));
        assertEquals(-999, getClosestTo1000(-1000, -999));
    }

    @Test
    public void return1stNumberWhenBothAreCloseTo1000() {
        assertEquals(990, getClosestTo1000(990, 1010));
        assertEquals(1010, getClosestTo1000(1010, 990));
    }

    @Test
    public void returnClosedNumberTo1000InCaseOfMinAndMaxInt() {
        assertEquals(Integer.MAX_VALUE, getClosestTo1000(Integer.MAX_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestTo1000(Integer.MIN_VALUE, Integer.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, getClosestTo1000(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, getClosestTo1000(Integer.MAX_VALUE, Integer.MAX_VALUE));

        assertEquals(0, getClosestTo1000(0, Integer.MIN_VALUE));
        assertEquals(0, getClosestTo1000(0, Integer.MAX_VALUE));

        assertEquals(-10, getClosestTo1000(-10, Integer.MAX_VALUE));
        assertEquals(-10, getClosestTo1000(-10, Integer.MIN_VALUE));
    }
}
