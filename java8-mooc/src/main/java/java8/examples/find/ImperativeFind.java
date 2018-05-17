package java8.examples.find;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImperativeFind {

    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java7", "Clojure", "Scala", "Java8", "Groovy");
        findJava8(languages);
        findJavaVersions(languages);
    }

    public static void findJava8(List<String> languages) {
        boolean found = false;
        for (String language : languages) {
            if (language.equals("Java8")) {
                found = true;
                break;
            }
        }

        if (found)
            System.out.println("Found Java8");
        else
            System.out.println("Sorry, Java8 not found");
    }

    public static void findJavaVersions(List<String> languages) {
        List<String> javaVersions = new ArrayList<>();
        for (String language : languages) {
            if (language.startsWith("Java")) {
                javaVersions.add(language);
            }
        }

        System.out.println("Java versions: " + javaVersions);
    }
}
