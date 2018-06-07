package org.uma.jmetalsp.spark.streamingdatasource;

import org.apache.avro.generic.GenericData;
import org.apache.spark.rdd.RDD;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.spark.SparkStreamingDataSource;
import scala.Function1;
import scala.Tuple2;
import scala.runtime.BoxedUnit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class SimpleSparkStreamingCounterDataSource
        implements SparkStreamingDataSource<SingleObservedData<Integer>> , Serializable {
	private  Observable<SingleObservedData<Integer>> observable;

	private double time=1.0d;
	private int tauT=5;
	private int nT=10;

	private JavaStreamingContext streamingContext ;//transient
	private String directoryName ;


	public SimpleSparkStreamingCounterDataSource(
					Observable<SingleObservedData<Integer>> observable,
					String directoryName) {
		this.observable = observable ;
		this.directoryName = directoryName ;
	}

  public SimpleSparkStreamingCounterDataSource(String directoryName) {
    this(new DefaultObservable<>(), directoryName) ;
  }

	@Override
	public void run() {
		JMetalLogger.logger.info("Run method in the streaming data source invoked") ;
    JMetalLogger.logger.info("Directory: " + directoryName) ;

		JavaDStream<Integer> time = streamingContext
						.textFileStream(directoryName)
						.map(line -> Integer.parseInt(line)) ;


		time.foreachRDD(numbers -> {
			Integer value =numbers.reduce((integer, integer2) -> integer2);
       System.out.println("--------------"+value);
			observable.setChanged();
			observable.notifyObservers(new SingleObservedData<Integer>(value));


		});
		/*
			time.foreachRDD(numbers -> {
			numbers.foreach(integer -> {
				System.out.println("LEO->"+integer);
			//funcion(integer);
				observable.setChanged();
				observable.notifyObservers(new SingleObservedData<Integer>(integer));
			});

		});
		 */


/*time.foreachRDD(integerJavaRDD -> {

    integerJavaRDD.map(integer -> {observable.setChanged();
    System.out.print("LEOOO "+integer);
		observable.notifyObservers(new SingleObservedData<Integer>(integer));
    return integer;});

});*/

		/*time.foreachRDD(numbers -> {
            numbers.foreach(integer -> {
				System.out.println("LEO->"+integer);
				observable.setChanged();
			    observable.notifyObservers(new SingleObservedData<Integer>(integer));
			});

		}) ;*/

	/*time.foreachRDD(numbers -> {
			List<Integer> numberList = numbers.collect() ;
			for (Integer number : numberList) {
			  System.out.println(number) ;
        observable.setChanged();
				observable.notifyObservers(new SingleObservedData<Integer>(number));
			}
		}) ;*/
	}

  @Override
  public Observable<SingleObservedData<Integer>> getObservable() {
    return observable;
  }

  @Override
	public void setStreamingContext(JavaStreamingContext streamingContext) {
		this.streamingContext = streamingContext;
	}



}