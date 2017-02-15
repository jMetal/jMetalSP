package org.uma.jmetalsp.examples.dynamicnsgaii;

import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.khaos.perception.core.Observable;
import org.uma.khaos.perception.core2.ObservableItem;

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
	public void start() {
		int counter = 0 ;
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			updateData.hasChanged() ;
			updateData.notifyObservers(new FDAUpdateData(counter));
			counter ++ ;
		}
	}
}
