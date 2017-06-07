package org.uma.jmetalsp.problem.tsp;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.MatrixObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by cbarba on 07/06/2017.
 */
public class MultiobjectiveTSPBuilderFromNY {

  private String initialFile;
  private int         numberOfCities ;
  private double [][] distanceMatrix ;
  private double [][] costMatrix;
 public MultiobjectiveTSPBuilderFromNY (String initialFile){
   this.initialFile=initialFile;
 }
  public DynamicMultiobjectiveTSP build(Observable<MatrixObservedData<Double>> observable) throws IOException {

    readProblem(this.initialFile);

    DynamicMultiobjectiveTSP problem =
            new DynamicMultiobjectiveTSP(numberOfCities, distanceMatrix, costMatrix);

    observable.register(problem);

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
