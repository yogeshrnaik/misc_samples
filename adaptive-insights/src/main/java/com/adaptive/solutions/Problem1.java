package com.adaptive.solutions;

public class Problem1 {

    /**
     * Given two integers, returns the integer that is closed to 1000
     *
     * @param number1
     * @param number2
     * @return integer closed to 1000
     */
    public static int getClosestTo1000(final int number1, final int number2) {
        return getClosestTo(number1, number2, 1000);
    }

    /**
     * Given two integers and a number, returns the integer that is closed to the given number
     *
     * @param number1
     * @param number2
     * @param closedTo
     * @return integer closed to number given
     */
    public static int getClosestTo(final int number1, final int number2, final int closedTo) {
        // convert to long to avoid integer overflow
        final long absDiff1 = Math.abs(new Long(closedTo) - new Long(number1));
        final long absDiff2 = Math.abs(new Long(closedTo) - new Long(number2));

        return absDiff1 <= absDiff2 ? number1 : number2;
    }
}
