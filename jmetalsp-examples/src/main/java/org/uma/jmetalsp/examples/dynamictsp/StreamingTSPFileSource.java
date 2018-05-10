package org.uma.jmetalsp.examples.dynamictsp;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.problem.tsp.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

/**
 * This class emits a value periodically after a given delay (in milliseconds) for {@link DynamicMultiobjectiveTSP}
 * problem. The intervals of the random numbers should be adjusted per particular problem
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingTSPFileSource implements StreamingDataSource<SingleObservedData<TSPMatrixData>> {
  private Observable<SingleObservedData<TSPMatrixData>> observable;
  private int dataDelay ;

	public StreamingTSPFileSource(Observable<SingleObservedData<TSPMatrixData>> observable, int dataDelay) {
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

			int fileNumber  = JMetalRandom.getInstance().nextInt(1, 3) ;
			String fileName = "data/update"+fileNumber+".txt";
			File file = new File(fileName);
			Scanner sc=null;
			try {
				 sc = new Scanner(file);
				Scanner linea=null;
				while(sc.hasNextLine()){
					String aux = sc.nextLine();
					 linea = new Scanner(aux);
					linea.useDelimiter(" ");
					while (linea.hasNext()){
						String type = linea.next();
						if(type.equalsIgnoreCase("c")){
							type="COST";
						}else{
							type="VALUE";
						}
						int x = linea.nextInt();
						int y = linea.nextInt();
						double value = linea.nextDouble();
						observable.setChanged();
						observable.notifyObservers(new SingleObservedData<TSPMatrixData>(new TSPMatrixData(type, x, y, value)));
					}
				}
				linea.close();;

			}catch (Exception e){
		e.printStackTrace();
			}finally {
				sc.close();
			}
	  //int x = JMetalRandom.getInstance().nextInt(0, 100) ;
      //int y = JMetalRandom.getInstance().nextInt(0, 100) ;
      //double value = JMetalRandom.getInstance().nextDouble(1.0, 4000) ;
		//	observable.setChanged();
			//observable.notifyObservers(new SingleObservedData<TSPMatrixData>(new TSPMatrixData("COST", x, y, value)));
    }
	}

  @Override
  public Observable<SingleObservedData<TSPMatrixData>> getObservable() {
    return this.observable;
  }
}
