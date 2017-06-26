package org.uma.jmetalsp.problem.tsp;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.*;

/**
 * Class for initializing a dynamic multiobjective TSP from data files having format TSPLib. Both files must have the
 * same number of cities
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MultiobjectiveTSPBuilderFromTSPLIBFiles {

  private String distanceFileName;
  private String costFileName ;
  private Observable<SingleObservedData<TSPMatrixData>> observable ;

  public MultiobjectiveTSPBuilderFromTSPLIBFiles(String distanceFileName, String costFileName) {
    this(distanceFileName, costFileName, new DefaultObservable<>()) ;
  }

  public MultiobjectiveTSPBuilderFromTSPLIBFiles(String distanceFileName,
                                                 String costFileName,
                                                 Observable<SingleObservedData<TSPMatrixData>> observable) {
    this.distanceFileName = distanceFileName ;
    this.costFileName = costFileName ;
    this.observable = observable ;
  }

  public DynamicMultiobjectiveTSP build() throws IOException {
    int         numberOfCities ;
    double [][] distanceMatrix ;
    double [][] costMatrix;

    distanceMatrix = readProblem(distanceFileName) ;
    costMatrix = readProblem(costFileName) ;
    numberOfCities = distanceMatrix.length ;

    DynamicMultiobjectiveTSP problem = new DynamicMultiobjectiveTSP(numberOfCities, distanceMatrix, costMatrix, observable);

    return problem ;
  }

  private double [][] readProblem(String file) throws IOException {
    double [][] matrix = null;

    InputStream in = getClass().getResourceAsStream(file);
    if (in == null) {
      in = new FileInputStream(file) ;
    }
    InputStreamReader isr = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(isr);

    StreamTokenizer token = new StreamTokenizer(br);
    try {
      boolean found ;
      found = false ;

      token.nextToken();
      while(!found) {
        if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }

      token.nextToken() ;
      token.nextToken() ;

      int numberOfCities =  (int)token.nval ;

      matrix = new double[numberOfCities][numberOfCities] ;

      // Find the string SECTION
      found = false ;
      token.nextToken();
      while(!found) {
        if ((token.sval != null) &&
            ((token.sval.compareTo("SECTION") == 0)))
          found = true ;
        else
          token.nextToken() ;
      }

      double [] c = new double[2*numberOfCities] ;

      for (int i = 0; i < numberOfCities; i++) {
        token.nextToken() ;
        int j = (int)token.nval ;

        token.nextToken() ;
        c[2*(j-1)] = token.nval ;
        token.nextToken() ;
        c[2*(j-1)+1] = token.nval ;
      } // for

      double dist ;
      for (int k = 0; k < numberOfCities; k++) {
        matrix[k][k] = 0;
        for (int j = k + 1; j < numberOfCities; j++) {
          dist = Math.sqrt(Math.pow((c[k*2]-c[j*2]),2.0) +
              Math.pow((c[k*2+1]-c[j*2+1]), 2));
          dist = (int)(dist + .5);
          matrix[k][j] = dist;
          matrix[j][k] = dist;
        }
      }
    } catch (Exception e) {
      new JMetalException("MultiobjectiveTSPInitializer.readProblem(): error when reading data file " + e);
    }
    return matrix;
  }
}
