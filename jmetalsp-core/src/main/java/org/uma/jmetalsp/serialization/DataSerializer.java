package org.uma.jmetalsp.serialization;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class DataSerializer<S> {


    public byte[] serializeMessage(S clazz, String path) {

        byte[] result = null;
        try {

            File file = new File(path);

            Schema schema = new Schema.Parser().parse(file);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<S> dataFileWriter = new SpecificDatumWriter<S>(schema);
            dataFileWriter.write(clazz, encoder);
            encoder.flush();
            result=out.toByteArray();
            out.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

}
