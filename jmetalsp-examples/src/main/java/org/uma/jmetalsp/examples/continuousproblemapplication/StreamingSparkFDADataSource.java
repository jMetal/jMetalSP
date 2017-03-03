package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.perception.Observable;
import org.uma.jmetalsp.spark.SparkStreamingDataSource;
import org.uma.jmetalsp.updatedata.TimeUpdateData;
import org.uma.jmetalsp.updatedata.impl.DefaultTimeUpdateData;

import java.util.List;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingSparkFDADataSource implements SparkStreamingDataSource<TimeUpdateData, Observable<TimeUpdateData>> {
	private Observable<TimeUpdateData> updateData ;
	private int dataDelay ;

	private double time=1.0d;
	private int tauT=5;
	private int nT=10;

	private JavaStreamingContext streamingContext ;
	private String directoryName ;

	/**
   * @param updateData
   * @param dataDelay Delay in milliseconds
   */
	public StreamingSparkFDADataSource(
					Observable<TimeUpdateData> updateData,
					int dataDelay,
					String directoryName) {
		this.updateData = updateData ;
		this.dataDelay = dataDelay ;
		this.directoryName = directoryName ;
	}

	@Override
	public void run() {
		System.out.println("Run method in the streaming data source invoked") ;
		System.out.println("Directory: " + directoryName) ;

		JavaDStream<String> lines = streamingContext.textFileStream(directoryName);

		JavaDStream<Integer> time = lines.map(line -> {System.out.println(Integer.parseInt(line)); return Integer.parseInt(line); }) ;

		time.foreachRDD(numbers -> {
			List<Integer> numberList = numbers.collect() ;
			for (Integer number : numberList) {
				double value = (1.0d / (double) nT) * Math.floor((double) number / (double) tauT);

				updateData.setChanged();
				updateData.notifyObservers(new DefaultTimeUpdateData(value));
			}

		}) ;
	}

	@Override
	public void setStreamingContext(JavaStreamingContext streamingContext) {
		this.streamingContext = streamingContext;
	}
}
