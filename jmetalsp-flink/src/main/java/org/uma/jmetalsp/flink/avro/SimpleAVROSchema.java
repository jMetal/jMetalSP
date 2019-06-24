package org.uma.jmetalsp.flink.avro;

import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.formats.avro.typeutils.AvroTypeInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SimpleAVROSchema<T> implements SerializationSchema<T> ,DeserializationSchema<T> {
    private static final long serialVersionUID = 1L;

    private final Class<T> avroType;

    private transient DatumReader<T> reader;
    private transient DatumWriter<T> dataFileWriter;

    private transient BinaryDecoder decoder;
    private transient BinaryEncoder encoder;

    private transient String path;

    public SimpleAVROSchema(Class<T> avroType,String path) {
        this.avroType = avroType;
        this.path = path;
    }
    @Override
    public byte[] serialize(T obj) {
        byte[] serializedBytes = null;
        try {
            ensureInitializedSerializer();
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

    private void ensureInitializedSerializer() throws IOException {
        File file = new File(path);
        Schema schema = new Schema.Parser().parse(file);
        dataFileWriter = new SpecificDatumWriter<T>(schema);

    }

    @Override
    public T deserialize(byte[] message) {
        ensureInitialized();
        try {
            //decoder = DecoderFactory.get().binaryDecoder(message, decoder);
            File file = new File(path);
            Schema schema = new Schema.Parser().parse(file);
            SpecificDatumReader<T> reader = new SpecificDatumReader<>(schema);
            decoder = DecoderFactory.get().binaryDecoder(message,null);
            return reader.read(null, decoder);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void ensureInitialized() {
        if (reader == null) {
            if (org.apache.avro.specific.SpecificRecordBase.class.isAssignableFrom(avroType)) {
                reader = new SpecificDatumReader<T>(avroType);
            } else {
                reader = new ReflectDatumReader<T>(avroType);
            }
        }
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {

//        return TypeExtractor.getForClass(avroType);

        return BasicTypeInfo.getInfoFor(avroType);
    }
}
