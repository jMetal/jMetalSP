package org.uma.jmetalsp.problem.tsp;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by cbarba on 07/06/2017.
 */
public class MultiobjectiveTSPBuilderFromNYData {

  private String initialDataFile;
  private int         numberOfCities ;
  private double [][] distanceMatrix ;
  private double [][] costMatrix;
  private Observable<SingleObservedData<TSPMatrixData>> observable ;

  public MultiobjectiveTSPBuilderFromNYData (String initialDataFile){
    this(initialDataFile, new DefaultObservable<>()) ;
  }

  public MultiobjectiveTSPBuilderFromNYData (String initialDataFile, Observable<SingleObservedData<TSPMatrixData>> observable){
    this.initialDataFile = initialDataFile ;
    this.observable = observable ;
  }


  public DynamicMultiobjectiveTSP build() throws IOException {

    readProblem(this.initialDataFile);

    DynamicMultiobjectiveTSP problem =
            new DynamicMultiobjectiveTSP(numberOfCities, distanceMatrix, costMatrix);

    return problem ;
  }

  private void readProblem(String file) throws IOException {
    try {
      BufferedReader br = null;
      br = new BufferedReader(new FileReader(file));
      this.numberOfCities = Integer.parseInt(br.readLine());

      this.distanceMatrix = new double [numberOfCities][numberOfCities];
      this.costMatrix = new double [numberOfCities][numberOfCities];

      for (int i = 0; i < this.numberOfCities; i++) {
        for (int j = 0; j < this.numberOfCities; j++) {
          this.distanceMatrix[i][j] = Double.POSITIVE_INFINITY;
          this.costMatrix[i][j] = Double.POSITIVE_INFINITY;
        }
      }

      for(String line; (line = br.readLine()) != null; ) {
        String[] tokens = line.split(" ");
        int origin = Integer.parseInt(tokens[0]);
        int destin = Integer.parseInt(tokens[1]);
        int distan = Integer.parseInt(tokens[2]);
        double time = Double.parseDouble(tokens[3]);
        int nodeid = Integer.parseInt(tokens[4]);
        this.distanceMatrix[origin][destin] = distan;
        this.costMatrix[origin][destin] = time;
      }
    } catch (Exception e) {
      new JMetalException("MultiobjectiveTSPInitializer.readProblem(): error when reading data file " + e);
    }
  }

}
