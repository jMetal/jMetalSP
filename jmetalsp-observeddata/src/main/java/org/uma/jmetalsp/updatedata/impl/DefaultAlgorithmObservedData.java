package org.uma.jmetalsp.updatedata.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.updatedata.AlgorithmResultData;

import java.util.List;

/**
 * Created by ajnebro on 16/2/17.
 */
public class DefaultAlgorithmObservedData implements AlgorithmResultData {
  private List<? extends Solution<?>> solutionList;
  private int numberOfIterations;
  private double computingTime;

  public DefaultAlgorithmObservedData(List<? extends Solution<?>> solutionList, int numberOfIterations, double computingTime) {
    this.solutionList = solutionList;
    this.computingTime = computingTime;
    this.numberOfIterations = numberOfIterations;
  }

  @Override
  public List<? extends Solution<?>> getSolutionList() {
    return solutionList;
  }

  @Override
  public double getRunningTime() {
    return computingTime;
  }

  @Override
  public int getIterations() {
    return numberOfIterations;
  }
}
