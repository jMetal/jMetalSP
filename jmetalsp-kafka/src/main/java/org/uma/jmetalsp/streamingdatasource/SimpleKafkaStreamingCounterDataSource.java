package org.uma.jmetalsp.streamingdatasource;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.uma.jmetalsp.KafkaStreamingDataSource;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.problem.fda.FDA;

import java.io.Serializable;

public class SimpleKafkaStreamingCounterDataSource implements
        KafkaStreamingDataSource<SingleObservedData<Integer>> , Serializable {
    private Observable<SingleObservedData<Integer>> observable;
    private String topic;
    private StreamsBuilder streamingBuilder;
    private KStream<Integer,Integer> counter;

    public SimpleKafkaStreamingCounterDataSource(Observable<SingleObservedData<Integer>> observable){
        this.observable = observable;
    }

    @Override
    public void setStreamingBuilder(StreamsBuilder streamingBuilder) {
        this.streamingBuilder = streamingBuilder;
    }

    @Override
    public void setTopic(String topic) {
        this.topic=topic;

    }

    @Override
    public void run() {
        counter = streamingBuilder.stream(topic);
        counter.foreach((key, value) -> {
            observable.setChanged();
            observable.notifyObservers(new SingleObservedData<Integer>(value));
        });

    }

    @Override
    public Observable<SingleObservedData<Integer>> getObservable() {
        return observable;
    }
}
