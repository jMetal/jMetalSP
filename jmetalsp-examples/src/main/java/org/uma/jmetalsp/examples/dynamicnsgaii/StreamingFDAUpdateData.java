package org.uma.jmetalsp.examples.dynamicnsgaii;

import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.khaos.perception.core.Observable;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingFDAUpdateData implements StreamingDataSource<FDAUpdateData, Observable<FDAUpdateData>> {
	private Observable<FDAUpdateData> updateData ;

	public StreamingFDAUpdateData(Observable<FDAUpdateData> updateData) {
		this.updateData = updateData ;
	}

	@Override
	public void run() {
		int counter = 0 ;
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateData.setChanged(); ;
			updateData.notifyObservers(new FDAUpdateData(counter));
			counter ++ ;
		}
	}
}
