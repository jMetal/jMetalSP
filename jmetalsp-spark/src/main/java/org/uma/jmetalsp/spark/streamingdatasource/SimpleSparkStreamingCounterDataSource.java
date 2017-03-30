package org.uma.jmetalsp.spark.streamingdatasource;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.perception.Observable;
import org.uma.jmetalsp.spark.SparkStreamingDataSource;

import java.util.List;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleSparkStreamingCounterDataSource implements SparkStreamingDataSource<SingleObservedData<Integer>, Observable<SingleObservedData<Integer>>> {
	private Observable<SingleObservedData<Integer>> updateData ;

	private double time=1.0d;
	private int tauT=5;
	private int nT=10;

	private JavaStreamingContext streamingContext ;
	private String directoryName ;

	/**
   * @param observedData
   */
	public SimpleSparkStreamingCounterDataSource(
					Observable<SingleObservedData<Integer>> observedData,
					String directoryName) {
		this.updateData = observedData ;
		this.directoryName = directoryName ;
	}

	@Override
	public void run() {
		JMetalLogger.logger.info("Run method in the streaming data source invoked") ;
    JMetalLogger.logger.info("Directory: " + directoryName) ;

		JavaDStream<Integer> time = streamingContext
						.textFileStream(directoryName)
						.map(line -> Integer.parseInt(line)) ;

		time.foreachRDD(numbers -> {
			List<Integer> numberList = numbers.collect() ;
			for (Integer number : numberList) {
        updateData.setChanged();
				updateData.notifyObservers(new SingleObservedData<Integer>(number));
			}

		}) ;
	}

	@Override
	public void setStreamingContext(JavaStreamingContext streamingContext) {
		this.streamingContext = streamingContext;
	}
}
