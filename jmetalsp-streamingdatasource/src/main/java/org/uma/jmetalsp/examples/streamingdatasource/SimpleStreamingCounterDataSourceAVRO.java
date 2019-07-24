package org.uma.jmetalsp.examples.streamingdatasource;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.serialization.counter.Counter;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleStreamingCounterDataSourceAVRO
		implements StreamingDataSource<ObservedValue<Counter>> {
	private Observable<ObservedValue<Counter>> observable;
	private int dataDelay ;

	/**
	 *
	 * @param observable
	 * @param dataDelay Delay in milliseconds
	 */
	public SimpleStreamingCounterDataSourceAVRO(Observable<ObservedValue<Counter>> observable, int dataDelay) {
		this.observable = observable ;
		this.dataDelay = dataDelay ;
	}

	public SimpleStreamingCounterDataSourceAVRO(int dataDelay) {
		this(new DefaultObservable<>(), dataDelay);
	}

	public SimpleStreamingCounterDataSourceAVRO(int dataDelay, Observable<ObservedValue<Counter>> observable) {
		this(observable, dataDelay) ;
	}

	@Override
	public void run() {
		int count = 0 ;
		while (true) {
			try {
				Thread.sleep(dataDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Counter counter = new Counter(count);
			observable.setChanged(); ;
			observable.notifyObservers(new ObservedValue<>(counter));
			count ++ ;
		}
	}

	@Override
	public Observable<ObservedValue<Counter>> getObservable() {
		return this.observable;
	}


	/**
	 * main() method to run the streaming por
	 * @param args
	 */

	public static void main(String[] args) {
		String topicName = "prueba-int-topic-from-main" ;

		SimpleStreamingCounterDataSourceAVRO simpleStreamingCounterDataSource =
				new SimpleStreamingCounterDataSourceAVRO(
						new KafkaObservable<>(topicName, "avsc/Counter.avsc"), 2000) ;

		simpleStreamingCounterDataSource.run();
	}
}
