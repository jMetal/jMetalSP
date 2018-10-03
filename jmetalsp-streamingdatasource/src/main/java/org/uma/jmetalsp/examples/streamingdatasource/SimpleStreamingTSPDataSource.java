package org.uma.jmetalsp.examples.streamingdatasource;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleStreamingTSPDataSource
		implements StreamingDataSource<ObservedValue<TSPMatrixData>> {
	private Observable<ObservedValue<TSPMatrixData>> observable;
	private int dataDelay ;

	/**
	 *
	 * @param observable
	 * @param dataDelay Delay in milliseconds
	 */
	public SimpleStreamingTSPDataSource(Observable<ObservedValue<TSPMatrixData>> observable, int dataDelay) {
		this.observable = observable ;
		this.dataDelay = dataDelay ;
	}

	public SimpleStreamingTSPDataSource(int dataDelay) {
		this(new DefaultObservable<>(), dataDelay);
	}

	public SimpleStreamingTSPDataSource(int dataDelay, Observable<ObservedValue<TSPMatrixData>> observable) {
		this(observable, dataDelay) ;
	}

	@Override
	public void run() {
		int counter = 0 ;
		while (true) {
			try {
				Thread.sleep(dataDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			observable.setChanged(); ;
			//observable.notifyObservers(new ObservedValue<>(counter));
			counter ++ ;
		}
	}

	@Override
	public Observable<ObservedValue<TSPMatrixData>> getObservable() {
		return this.observable;
	}


	/**
	 * main() method to run the streaming por
	 * @param args
	 */

	public static void main(String[] args) {
		String topicName = "prueba-tsp-topic-from-main" ;

		SimpleStreamingTSPDataSource simpleStreamingCounterDataSource =
				new SimpleStreamingTSPDataSource(
						new KafkaObservable<>(topicName, new ObservedValue<>()), 2000) ;

		simpleStreamingCounterDataSource.run();
	}
}
