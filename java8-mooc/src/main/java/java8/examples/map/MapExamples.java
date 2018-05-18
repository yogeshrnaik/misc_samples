package java8.examples.map;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MapExamples {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Long> creationTimestamps = new HashMap<>();
        creationTimestamps.put("Java7", Instant.now().toEpochMilli());

        Thread.sleep(2000);

        System.out.println("Timestamp for Java8 : " + getTimestamp(creationTimestamps, "Java8"));
        System.out.println("Timestamp for Java8 : " + getTimestampIfPresent(creationTimestamps, "Java8"));
        System.out.println("***********************************************************");
        System.out.println("Timestamp for Java7 : " + getTimestamp(creationTimestamps, "Java7"));
        System.out.println("Timestamp for Java7 : " + getTimestampIfPresent(creationTimestamps, "Java7"));
    }

    private static long getTimestamp(Map<String, Long> languageCounts, String language) {
        if (!languageCounts.containsKey(language)) {
            languageCounts.put(language, Instant.now().toEpochMilli());
        }
        return languageCounts.get(language);
    }

    private static long getTimestampIfPresent(Map<String, Long> languageCounts, String language) {
        return languageCounts.putIfAbsent(language, Instant.now().toEpochMilli());
    }
}
