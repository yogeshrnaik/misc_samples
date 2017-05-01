package com.adaptive.solutions;

import java.util.stream.Collectors;

public class Problem4 {

    private static final String ASTERIK = "*";

    /**
     * Returns a string with an asterisk inserted between every character in the original.<br>
     * For example, for "ab" returns "a*b" <br>
     * If string is null or empty or has only one character then same string is returned. <br>
     *
     * @param input
     * @return
     */
    public static String insertAsterikRecursive(String input) {
        return insertRecursive(input, new StringBuilder(), ASTERIK);
    }

    /**
     * Recursive method that takes input string, string builder to append the resulting string and string to insert. <br>
     *
     * @param input
     * @param result
     * @param strToInsert
     * @return
     */
    private static String insertRecursive(String input, StringBuilder result, String strToInsert) {
        if (input == null || input.length() <= 1) {
            // if input is null or empty or has only one character then return input as it is
            return result.length() == 0 ? input : result.append(input.charAt(0)).toString();
        }

        return insertRecursive(input.substring(1), result.append(input.charAt(0)).append(strToInsert), strToInsert);
    }

    /**
     * Returns a string with an asterisk inserted between every character in the original.<br>
     * For example, for "ab" returns "a*b" <br>
     * If string is null or empty or has only one character then same string is returned. <br>
     *
     * @param input
     * @return
     */
    public static String insertAsterikWithLoop(String input) {
        return insertWithLoop(input, ASTERIK);
    }

    /**
     * Method that takes input string and string to insert and uses loop to insert the string between every character of input string.
     *
     * @param input
     * @param strToInsert
     * @return
     */
    private static String insertWithLoop(String input, String strToInsert) {
        if (input == null || input.length() <= 1) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            result.append(ch).append(strToInsert);
        }

        // remove last occurrence of strToInsert before returning the result
        return result.substring(0, result.length() - strToInsert.length()).toString();
    }

    /**
     * Returns a string with an asterisk inserted between every character in the original.<br>
     * For example, for "ab" returns "a*b" <br>
     * If string is null or empty or has only one character then same string is returned. <br>
     *
     * @param input
     * @return
     */
    public static String insertAsterikWithStream(String input) {
        return insertWithStream(input, ASTERIK);
    }

    private static String insertWithStream(String input, String strToInsert) {
        if (input == null || input.length() <= 1) {
            return input;
        }

        return input.chars()
            .mapToObj(ch -> Character.toString((char)ch))
            .collect(Collectors.joining(strToInsert));
    }
}
