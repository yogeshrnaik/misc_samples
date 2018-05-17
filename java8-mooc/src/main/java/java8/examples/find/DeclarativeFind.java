package java8.examples.find;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DeclarativeFind {

    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java7", "Clojure", "Scala", "Java8", "Groovy");
        findJava8(languages);
        findJavaVersions(languages);
    }

    public static void findJava8(List<String> languages) {
        if (languages.contains("Java8"))
            System.out.println("Found Java8");
        else
            System.out.println("Sorry, Java8 not found");
    }

    public static void findJavaVersions(List<String> languages) {
        List<String> javaVersions = languages.stream()
            .filter(l -> l.startsWith("Java"))
            .collect(Collectors.toList());
        System.out.println("Java versions: " + javaVersions);
    }
}
