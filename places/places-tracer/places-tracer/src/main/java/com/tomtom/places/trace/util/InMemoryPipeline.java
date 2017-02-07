package com.tomtom.places.trace.util;

import java.util.List;

import org.apache.hadoop.conf.Configuration;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.PTable;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.PipelineResult;
import com.cloudera.crunch.Source;
import com.cloudera.crunch.TableSource;
import com.cloudera.crunch.Target;
import com.cloudera.crunch.impl.mem.MemPipeline;
import com.google.common.collect.Lists;

public class InMemoryPipeline implements Pipeline {

    private Pipeline pipeline = MemPipeline.getInstance();

    /**
     * Result is stored in memory instead of writing to disk.
     */
    private List<?> result = null;

    /**
     * Returns the result that was stored as part of write() method.
     */
    public List<?> getResult() {
        return result;
    }

    public void setConfiguration(Configuration conf) {
        pipeline.setConfiguration(conf);

    }

    public Configuration getConfiguration() {
        return pipeline.getConfiguration();
    }

    public <T> PCollection<T> read(Source<T> source) {
        return pipeline.read(source);
    }

    public <K, V> PTable<K, V> read(TableSource<K, V> tableSource) {
        return pipeline.read(tableSource);
    }

    /**
     * Do not delegate the call to MemPipeline.write() because it fails with java.lang.UnsatisfiedLinkError:
     * org.apache.hadoop.util.NativeCrc32.nativeComputeChunkedSumsByteArray(II[BI[BIILjava/lang/String;JZ)V <br>
     * Instead materialize the PCollection and store it into "result". Use getResult() to access it later for verification.
     */
    public void write(PCollection<?> collection, Target target) {
        result = Lists.newArrayList(collection.materialize().iterator());
    }

    public <T> Iterable<T> materialize(PCollection<T> pcollection) {
        return pipeline.materialize(pcollection);
    }

    public PipelineResult run() {
        return pipeline.run();
    }

    public PipelineResult done() {
        return pipeline.done();
    }

    public PCollection<String> readTextFile(String pathName) {
        return pipeline.readTextFile(pathName);
    }

    /**
     * Do not delegate the call to MemPipeline.write() because it fails with java.lang.UnsatisfiedLinkError:
     * org.apache.hadoop.util.NativeCrc32.nativeComputeChunkedSumsByteArray(II[BI[BIILjava/lang/String;JZ)V
     */
    public <T> void writeTextFile(PCollection<T> collection, String pathName) {
        // pipeline.writeTextFile(collection, pathName);
    }

    public void enableDebug() {
        pipeline.enableDebug();
    }

}
