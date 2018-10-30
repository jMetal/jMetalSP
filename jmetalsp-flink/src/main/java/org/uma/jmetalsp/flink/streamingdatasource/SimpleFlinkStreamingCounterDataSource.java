package org.uma.jmetalsp.flink.streamingdatasource;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.flink.FlinkStreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.IOException;
import java.util.Iterator;

public class SimpleFlinkStreamingCounterDataSource implements FlinkStreamingDataSource<ObservedValue<Integer>> {
    private Observable<ObservedValue<Integer>> observable;
    private String directoryName;
    private StreamExecutionEnvironment environment;

    public SimpleFlinkStreamingCounterDataSource(
            Observable<ObservedValue<Integer>> observable,
            String directoryName) {
        this.observable = observable ;
        this.directoryName = directoryName ;
    }

    public SimpleFlinkStreamingCounterDataSource(String directoryName) {
        this(new DefaultObservable<>(), directoryName) ;
    }
    @Override
    public void setExecutionEnvironment(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void run() {

        JMetalLogger.logger.info("Run Fink method in the streaming data source invoked") ;
        JMetalLogger.logger.info("Directory: " + directoryName) ;
        DataStreamSource<String> data =environment.readTextFile(directoryName);
        try {
            Iterator<String> it=DataStreamUtils.collect(data);
            while (it.hasNext()){
                Integer number = Integer.parseInt(it.next());
                observable.setChanged();
                observable.notifyObservers(new ObservedValue<Integer>(number));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Observable<ObservedValue<Integer>> getObservable() {
        return this.observable;
    }
}
