package org.uma.jmetalsp.examples.dynamicnsgaii;

import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.util.Observable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingFDAUpdateData implements StreamingDataSource<FDAUpdateData, Observable<FDAUpdateData>> {
	private Observable<FDAUpdateData> updateData ;
	private int dataDelay ;

  /**
   *
   * @param updateData
   * @param dataDelay Delay in milliseconds
   */
	public StreamingFDAUpdateData(Observable<FDAUpdateData> updateData, int dataDelay) {
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
