package com.tomtom.places.misc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadBigFile {

    public static void main(String[] args) throws IOException {
        Files.lines(Paths.get("E:/Places/documents/GAPFM/Resume/ITA/before-RESUME/ITA/gp3ct/ITA_POI.txt"))
            .parallel()
            .filter(l -> !l.contains("Entry Point") && l.contains("380009003020566"))
            .forEach(System.out::println);
    }
}
