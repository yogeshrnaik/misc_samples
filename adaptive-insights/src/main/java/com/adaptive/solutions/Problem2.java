package com.adaptive.solutions;

import java.util.stream.Collectors;

public class Problem2 {

    /**
     * Returns a string where every character in the original is doubled. <br>
     * For example, given the string "xyz", return the string "xxyyzz". <br>
     * In case of null or emptry string, returns null or empty string.
     *
     * @param input
     * @return new string with every character doubled in input string
     */
    public static String doubleEveryCharWithRegex(String input) {
        return input != null ? input.replaceAll(".", "$0$0") : null;
    }

    /**
     * Returns a string where every character in the original is doubled. <br>
     * For example, given the string "xyz", return the string "xxyyzz". <br>
     * In case of null or emptry string, returns null or empty string.
     *
     * @param input
     * @return new string with every character doubled in input string
     */
    public static String doubleEveryCharWithLoop(String input) {
        if (input == null) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            result.append(ch).append(ch);
        }
        return result.toString();

    }

    /**
     * Returns a string where every character in the original is doubled. <br>
     * For example, given the string "xyz", return the string "xxyyzz". <br>
     * In case of null or emptry string, returns null or empty string.
     *
     * @param input
     * @return new string with every character doubled in input string
     */
    public static String doubleEveryCharWithStream(String input) {
        if (input == null) {
            return input;
        }

        return input.chars()
            .mapToObj(ch -> (char)ch)
            .map(ch -> Character.toString(ch) + Character.toString(ch))
            .collect(Collectors.joining());
    }
}
