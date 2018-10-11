package org.uma.jmetalsp.problem.tsp;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

/**
 * Version of the multi-objective TSP aimed at being solving dynamically.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicMultiobjectiveTSP
    extends AbstractIntegerPermutationProblem
    implements ConstrainedProblem<PermutationSolution<Integer>>,
        DynamicProblem<PermutationSolution<Integer>, ObservedValue<TSPMatrixData>> {

  public static final double NON_CONNECTED = Double.POSITIVE_INFINITY ;
  private int         numberOfCities ;
  private double [][] distanceMatrix ;
  private double [][] costMatrix;
  private Observable<ObservedValue<TSPMatrixData>> observable ;

  private boolean theProblemHasBeenModified;

  public OverallConstraintViolation<PermutationSolution<Integer>> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<PermutationSolution<Integer>> numberOfViolatedConstraints ;

  public DynamicMultiobjectiveTSP(int numberOfCities,
                                  double[][] distanceMatrix,
                                  double[][] costMatrix,
                                  Observable<ObservedValue<TSPMatrixData>> observable) {
    this.numberOfCities = numberOfCities ;
    this.distanceMatrix = distanceMatrix ;
    this.costMatrix = costMatrix ;
    this.observable = observable ;

    theProblemHasBeenModified = false ;

    setName("DMoTSP");
    setNumberOfVariables(numberOfCities);
    setNumberOfObjectives(2);
    setNumberOfConstraints(1);

    overallConstraintViolationDegree = new OverallConstraintViolation<PermutationSolution<Integer>>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<PermutationSolution<Integer>>() ;
  }

  public DynamicMultiobjectiveTSP(int numberOfCities,
                                  double[][] distanceMatrix,
                                  double[][] costMatrix) {
    this(numberOfCities, distanceMatrix, costMatrix, new DefaultObservable<>()) ;
  }

  @Override
  public synchronized void evaluate(PermutationSolution<Integer> solution) {
    double fitness1   ;
    double fitness2   ;

    fitness1 = 0.0 ;
    fitness2 = 0.0 ;

    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.getVariableValue(i) ;
      y = solution.getVariableValue(i+1) ;

      if (distanceMatrix[x][y] != NON_CONNECTED) {
        fitness1 += distanceMatrix[x][y];
        fitness2 += costMatrix[x][y];
      }
    }
    int firstCity ;
    int lastCity  ;

    firstCity = solution.getVariableValue(0) ;
    lastCity = solution.getVariableValue(numberOfCities - 1) ;

    if (distanceMatrix[firstCity][lastCity] != NON_CONNECTED) {
      fitness1 += distanceMatrix[firstCity][lastCity];
      fitness2 += costMatrix[firstCity][lastCity];
    }

    solution.setObjective(0, fitness1);
    solution.setObjective(1, fitness2);
  }

  public synchronized void evaluateConstraints(PermutationSolution<Integer> solution) {
    int nonConnectedLinks = 0 ;
    int connectedLinks = 0;
    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.getVariableValue(i) ;
      y = solution.getVariableValue(i+1) ;

      if (distanceMatrix[x][y] == NON_CONNECTED) {
        nonConnectedLinks ++ ;
      }else{
        connectedLinks ++;
      }
    }

    int firstCity ;
    int lastCity  ;
    firstCity = solution.getVariableValue(0) ;
    lastCity = solution.getVariableValue(numberOfCities - 1) ;

    if (distanceMatrix[firstCity][lastCity] == NON_CONNECTED) {
      nonConnectedLinks ++ ;
    }else{
      connectedLinks ++;
    }
    if(connectedLinks>15){
      int x=0;
      x++;
    }
    overallConstraintViolationDegree.setAttribute(solution, -1.0 * nonConnectedLinks);
    numberOfViolatedConstraints.setAttribute(solution,nonConnectedLinks-numberOfCities+9);
    //System.out.println("Violation: " + nonConnectedLinks + ". Fitness: " + solution.getObjective(0)) ;
  }

  @Override
  public int getPermutationLength() {
    return numberOfCities;
  }

  public synchronized void updateCostValue(int row, int col, double newValue) {
    try {
      if (costMatrix.length < row && costMatrix[row].length < col) {
        costMatrix[row][col] = newValue;
        theProblemHasBeenModified = true;
      }
    }catch(Exception ex){

      }
    //JMetalLogger.logger.info("Updated cost: " + row + ", " + col + ": " + newValue) ;
  }

  public synchronized void updateDistanceValue(int row, int col, double newValue) {
    try {
      if (distanceMatrix.length < row && distanceMatrix[row].length < col) {
        distanceMatrix[row][col] = newValue;
        theProblemHasBeenModified = true;
      }
    }catch (Exception ex){

    }
  }

  public synchronized double[][] getDistanceMatrix() {
    return distanceMatrix;
  }

  public synchronized double[][] getCostMatrix() {
    return costMatrix;
  }

  public synchronized void setNumberOfCities(int numberOfCities) {
    this.numberOfCities = numberOfCities ;
  }

  public synchronized boolean hasTheProblemBeenModified() {
    return theProblemHasBeenModified;
  }

  @Override
  public synchronized void reset() {
    theProblemHasBeenModified = false ;
  }

  public String toString() {
    String result = "" ;
    for (int i = 0; i < numberOfCities; i++) {
      for (int j = 0; j < numberOfCities; j++) {
        result += "" +  distanceMatrix[i][j] + " " ;
      }
      result += "\n" ;
    }

    return result ;
  }

  @Override
  public void update(Observable<ObservedValue<TSPMatrixData>> observable, ObservedValue<TSPMatrixData> data) {
    if (data!=null && data.getValue().getMatrixIdentifier() == "COST") {
      updateCostValue(data.getValue().getX(),data.getValue().getY(),data.getValue().getValue());
    } else if(data!=null && data.getValue().getMatrixIdentifier() == "VALUE"){
      updateDistanceValue(data.getValue().getX(),data.getValue().getY(),data.getValue().getValue());
    }
  }
}
