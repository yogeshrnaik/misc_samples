package com.tomtom.places.archive.checker;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ArchiveCheckerTest {

    private static ArchiveChecker checker;
    private static final InMemoryPipeline memPipeline = new InMemoryPipeline();
    private TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void setup() throws IOException {
        temp = new TemporaryFolder();
        temp.create();

        checker = new ArchiveChecker("src/test/resources/archive-places", temp.getRoot().getAbsolutePath());
    }

    @After
    public void cleanup() {
        temp.delete();
    }

    @Test
    public void testArchiveChecker() {
        checker.run(memPipeline);
    }
}
