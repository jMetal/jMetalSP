package org.uma.jmetalsp.reader;

import org.uma.jmetalsp.problem.fda.FDA;
import org.uma.jmetalsp.producer.CounterProvider;

public class CounterReader extends Thread {
    private String topic;
    private FDA  problem;

    public CounterReader(String topic, FDA problem){

    }
}
