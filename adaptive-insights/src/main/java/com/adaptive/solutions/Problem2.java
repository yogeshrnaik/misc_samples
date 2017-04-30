package com.adaptive.solutions;

public class Problem2 {

    /**
     * Returns a string where every character in the original is doubled. <br>
     * For example, given the string "xyz", return the string "xxyyzz". <br>
     * In case of null or emptry string, returns null or empty string.
     *
     * @param input
     * @return new string with every character doubled in input string
     */
    public static String doubleEveryChar(String input) {
        return input != null ? input.replaceAll(".", "$0$0") : null;
    }
}
