package org.uma.jmetalsp.problem.tsp;

/**
 * Class representing the value of a TSP distance/time/cost matrix
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TSPMatrixData {
  private int x ;
  private int y ;
  private double value ;
  Object matrixIdentifier ;

  public TSPMatrixData(Object id, int x, int y, double value) {
    this.matrixIdentifier = id ;
    this.x = x ;
    this.y = y ;
    this.value = value ;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public double getValue() {
    return value;
  }

  public Object getMatrixIdentifier() {
    return matrixIdentifier;
  }
}
