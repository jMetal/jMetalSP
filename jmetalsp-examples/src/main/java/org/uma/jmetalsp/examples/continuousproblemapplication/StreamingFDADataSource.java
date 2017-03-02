package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.perception.Observable;
import org.uma.jmetalsp.updatedata.TimeUpdateData;
import org.uma.jmetalsp.updatedata.impl.DefaultTimeUpdateData;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingFDADataSource implements StreamingDataSource<TimeUpdateData, Observable<TimeUpdateData>> {
	private Observable<TimeUpdateData> updateData ;
	private int dataDelay ;

	private double time=1.0d;
	private int tauT=5;
	private int nT=10;

	/**
   *
   * @param updateData
   * @param dataDelay Delay in milliseconds
   */
	public StreamingFDADataSource(Observable<TimeUpdateData> updateData, int dataDelay) {
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

      time= (1.0d/(double)nT) * Math.floor((double)counter/(double)tauT) ;

			updateData.setChanged(); ;
			updateData.notifyObservers(new DefaultTimeUpdateData(time));
			counter ++ ;
		}
	}
}
