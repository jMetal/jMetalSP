package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.perception.Observable;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingFDADataSource implements StreamingDataSource<FDAUpdateData, Observable<FDAUpdateData>> {
	private Observable<FDAUpdateData> updateData ;
	private int dataDelay ;

  /**
   *
   * @param updateData
   * @param dataDelay Delay in milliseconds
   */
	public StreamingFDADataSource(Observable<FDAUpdateData> updateData, int dataDelay) {
		this.updateData = updateData ;
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
			updateData.setChanged(); ;
			updateData.notifyObservers(new FDAUpdateData(counter));
			counter ++ ;
		}
	}
}
