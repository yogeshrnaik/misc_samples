package com.adaptive.solutions;

public class Problem2 {

    public static String doubleEveryChar(String input) {
        return input.replaceAll(".", "$0$0");
    }
}
