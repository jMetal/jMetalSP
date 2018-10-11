package org.uma.jmetalsp.spark.streamingdatasource;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.spark.SparkStreamingDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleSparkStructuredKafkaStreamingCounter implements SparkStreamingDataSource<ObservedValue<Integer>> {
    private Observable<ObservedValue<Integer>> observable;
    private Map<String,Object> kafkaParams;
    private List<String> topic;
    private JavaStreamingContext streamingContext;
    public SimpleSparkStructuredKafkaStreamingCounter(Observable<ObservedValue<Integer>> observable,
    Map<String,Object> kafkaParams,String topic) {
        this.observable = observable;
        this.kafkaParams = kafkaParams;
        this.topic = new ArrayList<>();
        this.topic.add(topic);
    }
    public SimpleSparkStructuredKafkaStreamingCounter(
                                                      Map<String,Object> kafkaParams,String topic) {
       this(new DefaultObservable<>(),kafkaParams,topic);
    }

    @Override
    public void setStreamingContext(JavaStreamingContext streamingContext) {
        this.streamingContext = streamingContext;
    }

    @Override
    public void run() {

        ConsumerStrategy<Integer,Integer> consumerStrategy =ConsumerStrategies.Subscribe(topic,kafkaParams);
        LocationStrategy locationStrategy = LocationStrategies.PreferConsistent();

        JavaInputDStream<ConsumerRecord<Integer,Integer>> stream=
                (JavaInputDStream<ConsumerRecord<Integer,Integer>>)
                KafkaUtils.createDirectStream(streamingContext,
                locationStrategy,
                        consumerStrategy);



        JavaDStream<Integer> time=stream.map(value -> value.value() );
        /*time.foreachRDD(numbers->
                {
                    numbers.foreach(value->
                    {
                        System.out.println("Pruebas----> " + value);
                        observable.setChanged();
                        observable.notifyObservers(new SingleObservedData<Integer>(value));
                    });
                }
        );*/


            time.foreachRDD(numbers -> {
                Integer cont = numbers.reduce((key, value) -> value);
                //System.out.println("Pruebas----> " + cont);
                observable.setChanged();
                observable.notifyObservers(new ObservedValue<Integer>(cont));
            });

        
       // stream.foreachRDD((consumerRecordJavaRDD, time) -> consumerRecordJavaRDD.foreach(integer -> {
            //observable.setChanged();
            //observable.notifyObservers(new SingleObservedData<Integer>(integer.value()));
      //      System.out.println("Pruebas----> "+integer.value());
    //    }));

    }

    @Override
    public Observable<ObservedValue<Integer>> getObservable() {
        return observable;
    }
}
