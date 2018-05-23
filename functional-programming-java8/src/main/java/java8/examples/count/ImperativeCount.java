package java8.examples.count;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImperativeCount {

    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java7", "Clojure", "Scala", "Java8", "Groovy",
            "Java7", "Clojure", "Scala", "Java5", "Groovy");
        countLanguages(languages);
    }

    private static void countLanguages(List<String> languages) {
        Map<String, Integer> languageCounts = new HashMap<>();

        for (String languge : languages) {
            incrementLanguageCount(languageCounts, languge);
        }
        System.out.println(languageCounts);
    }

    private static void incrementLanguageCount(Map<String, Integer> languageCounts, String languge) {
        if (!languageCounts.containsKey(languge)) {
            languageCounts.put(languge, 0);
        }

        languageCounts.put(languge, languageCounts.get(languge) + 1);
    }

}
