package org.uma.jmetalsp.flink.avro;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

import java.io.File;

public class AvroDeserializationSchema<T> implements DeserializationSchema<T> {

    private static final long serialVersionUID = 1L;

    private final Class<T> avroType;

    private transient DatumReader<T> reader;
    private transient BinaryDecoder decoder;
    private transient String path;

    public AvroDeserializationSchema(Class<T> avroType,String path) {
        this.avroType = avroType;
        this.path = path;
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
        return TypeExtractor.getForClass(avroType);
    }
}