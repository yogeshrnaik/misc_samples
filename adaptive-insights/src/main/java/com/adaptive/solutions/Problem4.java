package com.adaptive.solutions;

public class Problem4 {

    public static String insertAsterik(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }

        return insertAsterikRecurse(input, "");
    }

    private static String insertAsterikRecurse(String input, String result) {
        if (input != null && input.length() == 1) {
            return result + input.charAt(0);
        }

        return insertAsterikRecurse(input.substring(1), result + input.charAt(0) + "*");
    }
}
