package com.adaptive.solutions;

public class Problem1 {

    /**
     * Given two integers, returns the integer that is close to 1000. <br>
     * If both numbers are close to 1000 then method returns the 1st input number.
     *
     * @param number1
     * @param number2
     * @return integer close to 1000
     */
    public static int getClosestTo1000(final int number1, final int number2) {
        return getClosestTo(number1, number2, 1000);
    }

    /**
     * Given two integers, returns the integer that is close to Integer.MIN_VALUE. <br>
     * If both numbers are close to Integer.MIN_VALUE then method returns the 1st input number.
     *
     * @param number1
     * @param number2
     * @return integer close to Integer.MIN_VALUE
     */
    public static int getClosestToMinInt(final int number1, final int number2) {
        return getClosestTo(number1, number2, Integer.MIN_VALUE);
    }

    /**
     * Given two integers, returns the integer that is close to Integer.MAX_VALUE. <br>
     * If both numbers are close to Integer.MAX_VALUE then method returns the 1st input number.
     *
     * @param number1
     * @param number2
     * @return integer close to Integer.MAX_VALUE
     */
    public static int getClosestToMaxInt(final int number1, final int number2) {
        return getClosestTo(number1, number2, Integer.MAX_VALUE);
    }

    /**
     * Given two integers and a number, returns the integer that is close to the given number. <br>
     * If both numbers are close to the given number then method returns the 1st input number.
     *
     * @param number1
     * @param number2
     * @param closeTo
     * @return integer close to number given
     */
    public static int getClosestTo(final int number1, final int number2, final int closeTo) {
        // convert to long to avoid integer overflow
        final long absDiff1 = Math.abs(toLong(closeTo) - toLong(number1));
        final long absDiff2 = Math.abs(toLong(closeTo) - toLong(number2));

        return absDiff1 <= absDiff2 ? number1 : number2;
    }

    /**
     * Converts an integer to long value
     *
     * @param number
     * @return
     */
    private static long toLong(int number) {
        return Long.valueOf(number);
    }
}
