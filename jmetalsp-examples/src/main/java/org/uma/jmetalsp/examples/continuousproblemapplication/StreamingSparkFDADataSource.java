package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.perception.Observable;
import org.uma.jmetalsp.spark.SparkStreamingDataSource;

import java.util.List;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingSparkFDADataSource implements SparkStreamingDataSource<SingleObservedData<Double>, Observable<SingleObservedData<Double>>> {
	private Observable<SingleObservedData<Double>> updateData ;

	private double time=1.0d;
	private int tauT=5;
	private int nT=10;

	private JavaStreamingContext streamingContext ;
	private String directoryName ;

	/**
   * @param observedData
   */
	public StreamingSparkFDADataSource(
					Observable<SingleObservedData<Double>> observedData,
					String directoryName) {
		this.updateData = observedData ;
		this.directoryName = directoryName ;
	}

	@Override
	public void run() {
		System.out.println("Run method in the streaming data source invoked") ;
		System.out.println("Directory: " + directoryName) ;

		JavaDStream<Integer> time = streamingContext
						.textFileStream(directoryName)
						.map(line -> Integer.parseInt(line)) ;

		time.foreachRDD(numbers -> {
			List<Integer> numberList = numbers.collect() ;
			for (Integer number : numberList) {
				double value = (1.0d / (double) nT) * Math.floor((double) number / (double) tauT);

				updateData.setChanged();
				updateData.notifyObservers(new SingleObservedData<Double>(value));
			}

		}) ;
	}

	@Override
	public void setStreamingContext(JavaStreamingContext streamingContext) {
		this.streamingContext = streamingContext;
	}
}
