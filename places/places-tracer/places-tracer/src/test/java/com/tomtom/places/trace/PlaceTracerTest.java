package com.tomtom.places.trace;

import org.junit.Test;

import com.tomtom.places.trace.util.InMemoryPipeline;

public class PlaceTracerTest {

    private static final String SINGLE_LOCALITY_RUN_DESCRIPTOR_PATH = "src/test/resources/artifact-repository/published/small-run"
        + "/run-descriptor/run-descriptor-integration-test.xml";

    private static final String MULTIPLE_LOCALITY_RUN_DESCRIPTOR_PATH = "src/test/resources/artifact-repository/published/integration-test"
        + "/run-descriptor/run-descriptor-integration-test.xml";
    private final InMemoryPipeline pipeline = new InMemoryPipeline();

    @Test
    public void testPlaceTracer() throws Exception {
        PlaceTracer tracker = new PlaceTracer(SINGLE_LOCALITY_RUN_DESCRIPTOR_PATH);
        tracker.run(pipeline);
    }
}
