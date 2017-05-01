package com.adaptive.solutions;

import java.util.Arrays;

public class Problem3 {

    /**
     * Returns true if there is a way to split the array in two so that the sum of the numbers on one side of the split equals the sum of
     * the numbers on the other side. <br>
     * Return false if input array is null or empty or has only one element.
     *
     * @param input
     * @return boolean
     */
    public static boolean isEqualSplitPossible(final int[] input) {
        if (input == null || input.length <= 1) {
            return false;
        }

        // map to long to avoid integer overflow while summing up
        final long sum = Arrays.stream(input).mapToLong(i -> new Long(i)).sum();

        return isEqualSplitPossible(input, sum);
    }

    private static boolean isEqualSplitPossible(final int[] input, final long sum) {
        long sumSoFar = 0;

        for (int index = 0; index < input.length - 1; index++) {
            sumSoFar += input[index];

            if (2 * sumSoFar == sum) {
                return true;
            }
        }

        return false;
    }
}
