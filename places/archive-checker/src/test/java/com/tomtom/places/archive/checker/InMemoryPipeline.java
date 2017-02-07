package com.tomtom.places.archive.checker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;

import com.cloudera.crunch.PCollection;
import com.cloudera.crunch.PTable;
import com.cloudera.crunch.Pair;
import com.cloudera.crunch.Pipeline;
import com.cloudera.crunch.PipelineResult;
import com.cloudera.crunch.Source;
import com.cloudera.crunch.TableSource;
import com.cloudera.crunch.Target;
import com.cloudera.crunch.impl.mem.MemPipeline;
import com.cloudera.crunch.io.avro.AvroFileTarget;
import com.google.common.collect.Lists;
import com.tomtom.places.unicorn.domain.avro.archive.ArchiveFallout;
import com.tomtom.places.unicorn.domain.avro.archive.RelatedArchivePlaceDiff;
import com.tomtom.places.unicorn.domain.avro.archive.RelatedArchivePlaceDiffCluster;

/**
 * In memory pipeline that wraps functionality of MemPipeline and overrides write() and writeText() methods to actually write the output
 * into Avro/Txt files respectively. <br>
 * It also stores the contents in memory into result map where key used is a resultCounter. <br>
 * 1st call to write() / writeText() having counter as 1 and 2nd having counter as 2 and so on. <br>
 * User can use getResult(int counter) method to get the List of in memory result. <br>
 * Also, write() method uses the resultCounter to create a file named "output_[resultCounter].avro"
 */
public class InMemoryPipeline implements Pipeline {

    private Pipeline pipeline = MemPipeline.getInstance();

    private static final Map<Class<?>, Schema> schemas = new HashMap<Class<?>, Schema>();
    static {
        schemas.put(RelatedArchivePlaceDiff.class, RelatedArchivePlaceDiff.SCHEMA$);
        schemas.put(RelatedArchivePlaceDiffCluster.class, RelatedArchivePlaceDiffCluster.SCHEMA$);
        schemas.put(ArchiveFallout.class, ArchiveFallout.SCHEMA$);
    }

    /**
     * Result is stored in memory instead of writing to disk.
     */
    private Map<Integer, List<?>> result = new LinkedHashMap<Integer, List<?>>();
    private int resultCounter = 0;

    /**
     * Returns the result that was stored as part of write() method.
     */
    public List<?> getResult(int resultCounter) {
        return result.get(resultCounter);
    }

    public List<?> getResult() {
        return result.get(resultCounter);
    }

    public Map<Integer, List<?>> getAllResults() {
        return result;
    }

    @Override
    public void setConfiguration(Configuration conf) {
        pipeline.setConfiguration(conf);

    }

    @Override
    public Configuration getConfiguration() {
        return pipeline.getConfiguration();
    }

    @Override
    public <T> PCollection<T> read(Source<T> source) {
        return pipeline.read(source);
    }

    @Override
    public <K, V> PTable<K, V> read(TableSource<K, V> tableSource) {
        return pipeline.read(tableSource);
    }

    /**
     * Do not delegate the call to MemPipeline.write() because it fails with java.lang.UnsatisfiedLinkError:
     * org.apache.hadoop.util.NativeCrc32.nativeComputeChunkedSumsByteArray(II[BI[BIILjava/lang/String;JZ)V <br>
     * Instead materialize the PCollection and store it into "result". Use getResult(resultCounter) to access it later for verification. <br>
     * Also, write file programmatically.
     */
    @Override
    public void write(PCollection<?> collection, Target target) {
        resultCounter++;

        List<?> data = Lists.newArrayList(collection.materialize().iterator());
        if (target instanceof AvroFileTarget) {
            AvroFileTarget t = (AvroFileTarget)target;
            writeToAvroFile(t.getPath().toString(), data, data.get(0).getClass());
        }
        result.put(resultCounter, data);
    }

    @SuppressWarnings("unchecked")
    private <T> void writeToAvroFile(String path, List<?> data, Class<T> clazz) {
        DataFileWriter<T> writer = null;
        try {
            DatumWriter<T> datumWriter = new SpecificDatumWriter<T>(clazz);
            writer = new DataFileWriter<T>(datumWriter);
            new File(path).mkdirs();
            FileOutputStream op = new FileOutputStream(path + File.separator + "output_" + resultCounter + ".avro", true);
            Schema schema = getSchema(clazz);
            writer.create(schema, op);
            for (Object o : data) {
                writer.append((T)o);
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while writing avro file: %s", path), e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private <T> Schema getSchema(Class<T> clazz) {
        Schema schema = schemas.get(clazz);
        if (schema == null) {
            throw new RuntimeException(String.format("Schema missing in InMemoryPipeline for %s", clazz.getName()));
        }
        return schema;
    }

    @Override
    public <T> Iterable<T> materialize(PCollection<T> pcollection) {
        return pipeline.materialize(pcollection);
    }

    @Override
    public PipelineResult run() {
        return pipeline.run();
    }

    @Override
    public PipelineResult done() {
        return pipeline.done();
    }

    @Override
    public PCollection<String> readTextFile(String pathName) {
        return pipeline.readTextFile(pathName);
    }

    /**
     * Do not delegate the call to MemPipeline.write() because it fails with java.lang.UnsatisfiedLinkError:
     * org.apache.hadoop.util.NativeCrc32.nativeComputeChunkedSumsByteArray(II[BI[BIILjava/lang/String;JZ)V<br>
     * Instead materialize the PCollection and store it into "result". Use getResult(resultCounter) to access it later for verification. <br>
     * Also, write file programmatically.
     */
    @Override
    public <T> void writeTextFile(PCollection<T> collection, String pathName) {
        resultCounter++;
        List<T> data = Lists.newArrayList(collection.materialize().iterator());
        writeText(collection, pathName);
        result.put(resultCounter, data);
    }

    private <T> void writeText(PCollection<?> collection, String pathName) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(pathName));
            if (collection instanceof PTable) {
                for (Object o : collection.materialize()) {
                    Pair p = (Pair)o;
                    writer.write(p.first().toString());
                    writer.write("\t");
                    writer.write(p.second().toString());
                    writer.write("\r\n");
                }
            } else {
                for (Object d : collection.materialize()) {
                    writer.write(d.toString() + "\r\n");
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while writing text file: %s", pathName), e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Override
    public void enableDebug() {
        pipeline.enableDebug();
    }

}
