package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.perception.Observable;
import org.uma.jmetalsp.updatedata.TimeObservedData;
import org.uma.jmetalsp.updatedata.impl.DefaultTimeObservedData;

/**
 * This class emits the value of a counter periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingFDADataSource implements StreamingDataSource<TimeObservedData, Observable<TimeObservedData>> {
	private Observable<TimeObservedData> observedData;
	private int dataDelay ;

	private double time=1.0d;
	private int tauT=5;
	private int nT=10;

	/**
   *
   * @param observedData
   * @param dataDelay Delay in milliseconds
   */
	public StreamingFDADataSource(Observable<TimeObservedData> observedData, int dataDelay) {
		this.observedData = observedData ;
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

			observedData.setChanged(); ;
			observedData.notifyObservers(new DefaultTimeObservedData(time));
			counter ++ ;
		}
	}
}
