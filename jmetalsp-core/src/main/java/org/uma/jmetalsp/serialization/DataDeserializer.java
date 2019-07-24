package org.uma.jmetalsp.serialization;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.EOFException;
import java.io.File;

public class DataDeserializer<S> {

    //"src/main/resources/TSPMatrixData.avsc"
    public S deserialize(byte [] event, String path){

        Decoder decoder = null;
        S result=null;
        try{
            File file = new File(path);
            Schema schema = new Schema.Parser().parse(file);
            SpecificDatumReader<S> reader = new SpecificDatumReader<>(schema);
            decoder = DecoderFactory.get().binaryDecoder(event,null);
            result= reader.read(null,decoder);
        }catch (EOFException ex){
            ex.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();

        }

        return result;
    }

}
