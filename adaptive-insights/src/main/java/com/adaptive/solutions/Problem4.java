package com.adaptive.solutions;

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
        if (input == null || input.length() <= 1) {
            return input;
        }

        return insertCharacterRecursive(input, new StringBuilder(), ASTERIK);
    }

    /**
     * Recursive method that takes input string, string builder to append the resulting string and string to insert. <br>
     *
     * @param input
     * @param result
     * @param strToInsert
     * @return
     */
    private static String insertCharacterRecursive(String input, StringBuilder result, String strToInsert) {
        if (input.length() == 1) {
            return result.append(input.charAt(0)).toString();
        }

        return insertCharacterRecursive(input.substring(1), result.append(input.charAt(0)).append(strToInsert), strToInsert);
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
        if (input == null || input.length() <= 1) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            result.append(ch).append(ASTERIK);
        }

        // remove last asterik before returning the result
        return result.deleteCharAt(result.length() - 1).toString();
    }
}
