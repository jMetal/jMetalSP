package org.uma.jmetalsp.streamingdatasource;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.uma.jmetalsp.KafkaStreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;

public class SimpleKafkaStreamingCounterDataSource implements
        KafkaStreamingDataSource<ObservedValue<Integer>> , Serializable {
    private Observable<ObservedValue<Integer>> observable;
    private String topic;
    private StreamsBuilder streamingBuilder;
    private KStream<Integer,Integer> counter;

    public SimpleKafkaStreamingCounterDataSource(Observable<ObservedValue<Integer>> observable){
        this.observable = observable;
    }
    public SimpleKafkaStreamingCounterDataSource(){
        this(new DefaultObservable<>()) ;
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
            observable.notifyObservers(new ObservedValue<Integer>(value));
        });

    }

    @Override
    public Observable<ObservedValue<Integer>> getObservable() {
        return observable;
    }
}
