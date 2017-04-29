package com.adaptive.solutions;

import java.util.Arrays;

public class Problem3 {

    public static boolean isSplitArrayInEqualPartsPossible(int[] input) {
        if (input == null || input.length <= 1) {
            return false;
        }

        int sum = Arrays.stream(input).sum();
        int sumSoFar = 0;

        for (int i = 0; i < input.length - 1; i++) {
            sumSoFar += input[i];
            if (sumSoFar * 2 == sum) {
                return true;
            }
        }

        return false;
    }
}
