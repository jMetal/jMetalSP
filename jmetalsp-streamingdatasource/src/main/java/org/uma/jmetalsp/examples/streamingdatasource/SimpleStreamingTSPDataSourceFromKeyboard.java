package org.uma.jmetalsp.examples.streamingdatasource;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;

import java.util.Scanner;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleStreamingTSPDataSourceFromKeyboard
		implements StreamingDataSource<ObservedValue<TSPMatrixData>> {
	private Observable<ObservedValue<TSPMatrixData>> observable;
	private int dataDelay ;

	/**
	 *
	 * @param observable
	 * @param dataDelay Delay in milliseconds
	 */
	public SimpleStreamingTSPDataSourceFromKeyboard(Observable<ObservedValue<TSPMatrixData>> observable, int dataDelay) {
		this.observable = observable ;
		this.dataDelay = dataDelay ;
	}

	public SimpleStreamingTSPDataSourceFromKeyboard(int dataDelay) {
		this(new DefaultObservable<>(), dataDelay);
	}

	public SimpleStreamingTSPDataSourceFromKeyboard(int dataDelay, Observable<ObservedValue<TSPMatrixData>> observable) {
		this(observable, dataDelay) ;
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);


		while (true) {
			System.out.println("Introduce the new TSPMatrix [Type, X, Y, Value] (between commas):");
			String s = scanner.nextLine() ;
			Scanner sl= new Scanner(s);
			sl.useDelimiter(",");
			TSPMatrixData tspMatrixData = new TSPMatrixData();
			try {
				String matrixID = sl.next().toUpperCase();
				int x = Integer.parseInt(sl.next());
				int y = Integer.parseInt(sl.next());
				double value = Double.parseDouble(sl.next());
				tspMatrixData.put(0,matrixID);
				tspMatrixData.put(1,x);
				tspMatrixData.put(2,y);
				tspMatrixData.put(3,value);
			}catch (Exception e){//any problem

			}
			System.out.println("TSP Matrix: " + tspMatrixData) ;

			observable.setChanged();

			observable.notifyObservers(new ObservedValue<>(tspMatrixData));
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

		SimpleStreamingTSPDataSourceFromKeyboard simpleStreamingCounterDataSource =
				new SimpleStreamingTSPDataSourceFromKeyboard(
						new KafkaObservable<>(topicName, "avsc/TSPMatrixData.avsc"), 2000) ;

		simpleStreamingCounterDataSource.run();
	}
}
