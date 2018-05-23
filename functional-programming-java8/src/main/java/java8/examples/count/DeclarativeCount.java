package java8.examples.count;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeclarativeCount {

    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java7", "Clojure", "Scala", "Java8", "Groovy",
            "Java7", "Clojure", "Scala", "Java5", "Groovy");
        countLanguages(languages);

        countLanguagesWithStream(languages);
    }

    private static void countLanguages(List<String> languages) {
        Map<String, Integer> languageCounts = new HashMap<>();
        for (String languge : languages) {
            incrementLanguageCount(languageCounts, languge);
        }
        System.out.println(languageCounts);
    }

    private static void incrementLanguageCount(Map<String, Integer> languageCounts, String languge) {
        languageCounts.merge(languge, 1, (oldValue, value) -> oldValue + value);
    }

    private static void countLanguagesWithStream(List<String> languages) {
        Map<String, Long> languageCounts = languages.stream()
            .collect(Collectors.groupingBy(lang -> lang, Collectors.counting()));

        System.out.println(languageCounts);

        languageCounts = languages.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println(languageCounts);
    }

}
