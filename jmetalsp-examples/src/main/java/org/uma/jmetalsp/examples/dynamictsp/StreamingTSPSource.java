package org.uma.jmetalsp.examples.dynamictsp;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observeddata.MatrixObservedData;

/**
 * This class emits a value periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingTSPSource implements StreamingDataSource<MatrixObservedData<Double>, Observable<MatrixObservedData<Double>>> {
	private Observable<MatrixObservedData<Double>> updateData ;
	private int dataDelay ;

  /**
   *
   * @param updateData
   * @param dataDelay Delay in milliseconds
   */
	public StreamingTSPSource(Observable<MatrixObservedData<Double>> updateData, int dataDelay) {
		this.updateData = updateData ;
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

			/*
      int x = JMetalRandom.getInstance().nextInt(0, 100) ;
      int y = JMetalRandom.getInstance().nextInt(0, 100) ;
      double value = JMetalRandom.getInstance().nextDouble(1.0, 4000) ;
			updateData.setChanged(); ;
			updateData.notifyObservers(new MatrixObservedData<>("COST", x, y, value));
			*/
			/*
			int x ;
			int y ;
			double value ;

      System.out.println("Emitting new data") ;

      x = 0 ;
      y = 0 ;
      value = 0 ;
      updateData.setChanged();
			updateData.notifyObservers(new MatrixObservedData<Double>("COST", x , y, value));

      try {
        Thread.sleep(dataDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Emitting new data 1") ;

      x = 20 ;
      y = 20 ;
      value = 0 ;
      updateData.setChanged();
      updateData.notifyObservers(new MatrixObservedData<Double>("COST", x , y, value));

      try {
        Thread.sleep(dataDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Emitting new data 2") ;

      x = 40 ;
      y = 40 ;
      value = 0 ;
      updateData.setChanged();
      updateData.notifyObservers(new MatrixObservedData<Double>("COST", x , y, value));

      try {
        Thread.sleep(dataDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Emitting new data 3") ;

      x = 40 ;
      y = 60 ;
      value = 0 ;
      updateData.setChanged();
      updateData.notifyObservers(new MatrixObservedData<Double>("DISTANCE", x , y, value));

      try {
        Thread.sleep(dataDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Emitting new data 4") ;

      x = 70 ;
      y = 70 ;
      value = 0 ;
      updateData.setChanged();
      updateData.notifyObservers(new MatrixObservedData<Double>("DISTANCE", x , y, value));

      try {
        Thread.sleep(dataDelay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }*/
    }
	}
}
