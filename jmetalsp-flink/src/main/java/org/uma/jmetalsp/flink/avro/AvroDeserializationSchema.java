package org.uma.jmetalsp.flink.avro;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

public class AvroDeserializationSchema<T> implements DeserializationSchema<T> {

    private static final long serialVersionUID = 1L;

    private final Class<T> avroType;

    private transient DatumReader<T> reader;
    private transient BinaryDecoder decoder;

    public AvroDeserializationSchema(Class<T> avroType) {
        this.avroType = avroType;
    }

    @Override
    public T deserialize(byte[] message) {
        ensureInitialized();
        try {
            decoder = DecoderFactory.get().binaryDecoder(message, decoder);
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