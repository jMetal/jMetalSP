package org.uma.jmetalsp.serialization.counter;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

public class Counter extends SpecificRecordBase implements SpecificRecord {
    private int value;
    public Counter(int value){
        this.value = value;
    }
    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public Object get(int i) {
        return value;
    }

    @Override
    public void put(int i, Object o) {
        value =(Integer)o;

    }
}
