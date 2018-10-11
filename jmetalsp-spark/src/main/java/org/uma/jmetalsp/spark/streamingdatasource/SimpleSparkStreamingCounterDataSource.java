package org.uma.jmetalsp.spark.streamingdatasource;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.spark.SparkStreamingDataSource;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class SimpleSparkStreamingCounterDataSource
        implements SparkStreamingDataSource<ObservedValue<Integer>> {
    private Observable<ObservedValue<Integer>> observable;

    private JavaStreamingContext streamingContext;
    private String directoryName;


    public SimpleSparkStreamingCounterDataSource(
            Observable<ObservedValue<Integer>> observable,
            String directoryName) {
        this.observable = observable;
        this.directoryName = directoryName;
    }

    public SimpleSparkStreamingCounterDataSource(String directoryName) {
        this(new DefaultObservable<>(), directoryName);
    }

    @Override
    public void run() {
        JMetalLogger.logger.info("Run method in the streaming data source invoked");
        JMetalLogger.logger.info("Directory: " + directoryName);

        JavaDStream<Integer> time = streamingContext
                .textFileStream(directoryName)
                .map(line -> Integer.parseInt(line));


        time.foreachRDD(numbers -> {
            if (numbers != null && numbers.rdd().count() > 0) {
                Integer cont = numbers.reduce((key, value) -> value);
                observable.setChanged();
                observable.notifyObservers(new ObservedValue<Integer>(cont));
            }
        });
		/*time.foreachRDD(numbers -> {
			List<Integer> numberList = numbers.collect() ;
			for (Integer number : numberList) {
			  System.out.println(number) ;
        observable.setChanged();
				observable.notifyObservers(new ObservedValue<Integer>(number));
			}
		}) ;*/
    }

    @Override
    public Observable<ObservedValue<Integer>> getObservable() {
        return observable;
    }

    @Override
    public void setStreamingContext(JavaStreamingContext streamingContext) {
        this.streamingContext = streamingContext;
    }

}