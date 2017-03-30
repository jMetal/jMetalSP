package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.perception.Observable;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleStreamingCounterDataSource implements StreamingDataSource<SingleObservedData<Integer>, Observable<SingleObservedData<Integer>>> {
	private Observable<SingleObservedData<Integer>> observable;
	private int dataDelay ;

	private double time=1.0d;
	private int tauT=5;
	private int nT=10;

	/**
   *
   * @param observable
   * @param dataDelay Delay in milliseconds
   */
	public SimpleStreamingCounterDataSource(Observable<SingleObservedData<Integer>> observable, int dataDelay) {
		this.observable = observable ;
		this.dataDelay = dataDelay ;
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
			observable.notifyObservers(new SingleObservedData<Integer>(counter));
			counter ++ ;
		}
	}
}
