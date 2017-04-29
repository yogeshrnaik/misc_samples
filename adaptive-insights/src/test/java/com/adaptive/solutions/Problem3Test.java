package com.adaptive.solutions;

import static com.adaptive.solutions.Problem3.isSplitArrayInEqualPartsPossible;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Problem3Test {

    @Test
    public void splitArrayInEqualPartsPossible() {
        assertTrue(isSplitArrayInEqualPartsPossible(new int[] {1, 1}));

        assertTrue(isSplitArrayInEqualPartsPossible(new int[] {1, 2, 3}));
        assertTrue(isSplitArrayInEqualPartsPossible(new int[] {0, 2, 2}));
        assertTrue(isSplitArrayInEqualPartsPossible(new int[] {-2, 2, 0, 0}));

        assertTrue(isSplitArrayInEqualPartsPossible(new int[] {1, 2, 3, 4, 10}));

        assertTrue(isSplitArrayInEqualPartsPossible(new int[] {10, 1, 2, 3, 4}));

        assertTrue(isSplitArrayInEqualPartsPossible(new int[] {-10, -1, -2, -3, -4}));
    }

    @Test
    public void splitArrayInEqualPartsNotPossible() {
        assertFalse(isSplitArrayInEqualPartsPossible(null));
        assertFalse(isSplitArrayInEqualPartsPossible(new int[] {}));
        assertFalse(isSplitArrayInEqualPartsPossible(new int[] {1}));
        assertFalse(isSplitArrayInEqualPartsPossible(new int[] {1, 2}));
        assertFalse(isSplitArrayInEqualPartsPossible(new int[] {2, 2, 2}));
    }
}
