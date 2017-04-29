package com.adaptive.solutions;

public class Problem1 {

    public static int getClosestTo1000(final int number1, final int number2) {
        final int closedTo = 1000;

        // cast to long to avoid integer overflow
        final long diff1 = new Long(closedTo) - new Long(number1);
        final long diff2 = new Long(closedTo) - new Long(number2);

        return Math.abs(diff1) <= Math.abs(diff2) ? number1 : number2;
    }
}
