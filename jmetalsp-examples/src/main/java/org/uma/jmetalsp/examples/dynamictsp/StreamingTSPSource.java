package org.uma.jmetalsp.examples.dynamictsp;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.problem.tsp.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;

/**
 * This class emits a value periodically after a given delay (in milliseconds) for {@link DynamicMultiobjectiveTSP}
 * problem. The intervals of the random numbers should be adjusted per particular problem
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingTSPSource implements StreamingDataSource<SingleObservedData<TSPMatrixData>> {
  private Observable<SingleObservedData<TSPMatrixData>> observable;
  private int dataDelay ;

	public StreamingTSPSource(Observable<SingleObservedData<TSPMatrixData>> observable, int dataDelay) {
		this.observable = observable ;
		this.dataDelay = dataDelay ;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(dataDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

      int x = JMetalRandom.getInstance().nextInt(0, 100) ;
      int y = JMetalRandom.getInstance().nextInt(0, 100) ;
      double value = JMetalRandom.getInstance().nextDouble(1.0, 4000) ;
			observable.setChanged();
			observable.notifyObservers(new SingleObservedData<TSPMatrixData>(new TSPMatrixData("COST", x, y, value)));
    }
	}

  @Override
  public Observable<SingleObservedData<TSPMatrixData>> getObservable() {
    return this.observable;
  }
}
