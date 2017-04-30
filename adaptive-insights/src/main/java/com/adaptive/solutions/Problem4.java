package com.adaptive.solutions;

public class Problem4 {

    /**
     * Returns a string with an asterisk inserted between every character in the original.<br>
     * For example, for "ab" returns "a*b" <br>
     * If string is null or empty or has only one character then same string is returned. <br>
     *
     * @param input
     * @return
     */
    public static String insertAsterik(String input) {
        if (input == null || input.length() <= 1) {
            return input;
        }

        return insertCharacterRecursive(input, "", "*");
    }

    private static String insertCharacterRecursive(String input, String result, String insert) {
        if (input.length() == 1) {
            return result + input.charAt(0);
        }

        return insertCharacterRecursive(input.substring(1), result + input.charAt(0) + insert, insert);
    }
}
