package com.adaptive.solutions;

import static com.adaptive.solutions.Problem3.isEqualSplitPossible;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Problem3Test {

    @Test
    public void splitArrayInEqualPartsPossibleForArrayWithSameTwoNumbers() {
        assertTrue(isEqualSplitPossible(toArray(0, 0)));
        assertTrue(isEqualSplitPossible(toArray(1, 1)));
        assertTrue(isEqualSplitPossible(toArray(-1, -1)));
    }

    @Test
    public void splitArrayInEqualPartsPossibleForArrayWithThreeNumbers() {
        assertTrue(isEqualSplitPossible(toArray(1, 2, 3)));
        assertTrue(isEqualSplitPossible(toArray(3, 1, 2)));
        assertTrue(isEqualSplitPossible(toArray(3, 2, 1)));

        assertTrue(isEqualSplitPossible(toArray(0, 2, 2)));
        assertTrue(isEqualSplitPossible(toArray(2, 0, 2)));
        assertTrue(isEqualSplitPossible(toArray(2, 2, 0)));
    }

    @Test
    public void splitArrayInEqualPartsPossibleForArrayWithFourNumbers() {
        assertTrue(isEqualSplitPossible(toArray(-2, 2, 0, 0)));
        assertTrue(isEqualSplitPossible(toArray(-1, 0, 2, 1)));
        assertTrue(isEqualSplitPossible(toArray(-1, 1, 1, -1)));
    }

    @Test
    public void splitArrayInEqualPartsPossibleForArrayWithAllPositiveNumbers() {
        assertTrue(isEqualSplitPossible(toArray(1, 2, 3, 4, 10)));
        assertTrue(isEqualSplitPossible(toArray(10, 1, 2, 3, 4)));

    }

    @Test
    public void splitArrayInEqualPartsPossibleForArrayWithAllNegativeNumbers() {
        assertTrue(isEqualSplitPossible(toArray(-10, -1, -2, -3, -4)));
        assertTrue(isEqualSplitPossible(toArray(-1, -2, -3, -4, -10)));
    }

    @Test
    public void splitArrayInEqualPartsNotPossibleForNullOrEmptyArray() {
        assertFalse(isEqualSplitPossible(null));
        assertFalse(isEqualSplitPossible(toArray()));
    }

    @Test
    public void splitArrayInEqualPartsNotPossibleForArrayHavingOnlyOneNumber() {
        assertFalse(isEqualSplitPossible(toArray(1)));
        assertFalse(isEqualSplitPossible(toArray(0)));
    }

    @Test
    public void splitArrayInEqualPartsNotPossibleForArrayHavingTwoNonEqualNumbers() {
        assertFalse(isEqualSplitPossible(toArray(1, 2)));
        assertFalse(isEqualSplitPossible(toArray(2, 1)));

        assertFalse(isEqualSplitPossible(toArray(-1, -2)));
        assertFalse(isEqualSplitPossible(toArray(-2, -1)));
    }

    @Test
    public void splitArrayInEqualPartsNotPossibleForArrayWithSameThreeNumbers() {
        assertFalse(isEqualSplitPossible(toArray(2, 2, 2)));
        assertFalse(isEqualSplitPossible(toArray(-2, -2, -2)));
    }

    @Test
    public void splitPossibleForArrayWithMinAndMaxIntValues() {
        assertTrue(isEqualSplitPossible(toArray(Integer.MAX_VALUE, Integer.MAX_VALUE)));
        assertTrue(isEqualSplitPossible(toArray(Integer.MIN_VALUE, Integer.MIN_VALUE)));

        assertTrue(isEqualSplitPossible(toArray(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE)));
        assertTrue(isEqualSplitPossible(toArray(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)));
    }

    @Test
    public void splitNotPossibleForArrayWithMinAndMaxIntValues() {
        assertFalse(isEqualSplitPossible(toArray(Integer.MIN_VALUE, Integer.MAX_VALUE)));
        assertFalse(isEqualSplitPossible(toArray(Integer.MAX_VALUE, Integer.MIN_VALUE)));

        assertFalse(isEqualSplitPossible(toArray(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)));
        assertFalse(isEqualSplitPossible(toArray(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE)));
    }

    private int[] toArray(int... numbers) {
        return numbers;
    }
}
