package org.uma.jmetalsp.flink.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AvroSerializationSchema<T> implements SerializationSchema<T> {

    private static final long serialVersionUID = 1L;

    private final Class<T> avroType;

    private transient DatumWriter<T> dataFileWriter;
    private transient BinaryEncoder encoder;
    private transient String path;

    public AvroSerializationSchema(Class<T> avroType, String path) {
        this.avroType = avroType;
        this.path = path;
    }

    @Override
    public byte[] serialize(T obj) {
        byte[] serializedBytes = null;
        try {
            ensureInitialized();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder = EncoderFactory.get().binaryEncoder(out, null);
            dataFileWriter.write(obj, encoder);
            encoder.flush();
            serializedBytes = out.toByteArray();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return serializedBytes;
    }

    private void ensureInitialized() throws IOException {
        File file = new File(path);
        Schema schema = new Schema.Parser().parse(file);
        dataFileWriter = new SpecificDatumWriter<T>(schema);

    }

}
