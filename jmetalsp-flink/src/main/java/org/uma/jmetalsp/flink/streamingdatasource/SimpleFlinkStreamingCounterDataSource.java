package org.uma.jmetalsp.flink.streamingdatasource;

import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.api.common.io.FilePathFilter;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategy;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.util.Collector;
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
    private long time;

    public SimpleFlinkStreamingCounterDataSource(
            Observable<ObservedValue<Integer>> observable,
            String directoryName) {
        this.observable = observable ;
        this.directoryName = directoryName ;
        this.time =1000;
    }

    public SimpleFlinkStreamingCounterDataSource(String directoryName) {
        this(new DefaultObservable<>(), directoryName) ;
    }
    @Override
    public void setExecutionEnvironment(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void setTime(long time) {
        this.time=time;
    }


    @Override
    public void run() {

        JMetalLogger.logger.info("Run Fink method in the streaming data source invoked") ;
        JMetalLogger.logger.info("Directory: " + directoryName) ;

       // environment.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(1,0));
        //environment.enableCheckpointing(10);
        Path filePath = new Path(directoryName);
        TextInputFormat inputFormat = new TextInputFormat(filePath);
        inputFormat.setFilesFilter(FilePathFilter.createDefaultFilter());
        DataStreamSource<String> data =environment.readFile(inputFormat,directoryName,
                FileProcessingMode.PROCESS_CONTINUOUSLY,time);


        try {
            Iterator<String> it=DataStreamUtils.collect(data);
            while (it.hasNext()){
                Integer number = Integer.parseInt(it.next());
                observable.setChanged();
                observable.notifyObservers(new ObservedValue<Integer>(number));
            }

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public Observable<ObservedValue<Integer>> getObservable() {
        return this.observable;
    }
}
