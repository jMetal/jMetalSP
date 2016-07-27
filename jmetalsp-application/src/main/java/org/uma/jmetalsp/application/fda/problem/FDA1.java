package org.uma.jmetalsp.application.fda.problem;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.problem.DynamicProblem;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by cris on 19/07/2016.
 */
public class FDA1 extends AbstractDoubleProblem implements DynamicProblem<DoubleSolution, FDAUpdateData> {

  private double time;
  private boolean theProblemHasBeenModified;

  public FDA1(){
    this(100,2);
  }
  public FDA1 (Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA1");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
    time=1.0d;
    theProblemHasBeenModified=false;
  }
  @Override
  public boolean hasTheProblemBeenModified() {
    return theProblemHasBeenModified;
  }

  @Override
  public void reset() {
    theProblemHasBeenModified = false ;
  }

  @Override
  public synchronized void  update(FDAUpdateData data) {
    time=data.getTime();
    theProblemHasBeenModified=true;
  }

  @Override
  public void evaluate(DoubleSolution solution) {

    int numberOfVariables = getNumberOfVariables();
    int numberOfObjectives = getNumberOfObjectives() ;
    if(numberOfObjectives==2) {
      double f;
      double[] x = new double[numberOfVariables];
      double g = 0;
      for (int i = 0; i < numberOfVariables; i++) {
        x[i] = solution.getVariableValue(i);
      }

      f = x[0];
      double gT = Math.sin(0.5 * Math.PI * time);
      //for(i=1;i<NV;i++){g+=pow((x[j]-sin(0.5*PI*t)),2);}
      for (int i = 1; i < numberOfVariables; i++) {
        g += Math.pow(x[i] - gT, 2);
      }
      g = g + 1;
      double h = 1 - Math.sqrt(f / g);
      solution.setObjective(0, f);
      solution.setObjective(1, h * g);
    }
  }
  /*
  public void evaluate(DoubleSolution solution) {

    int numberOfVariables = getNumberOfVariables();
    int numberOfObjectives = getNumberOfObjectives() ;
    if(numberOfObjectives==2) {
      double f;
      double[] x = new double[numberOfVariables];
      double g = 0;
      for (int i = 0; i < numberOfVariables; i++) {
        x[i] = solution.getVariableValue(i);
      }

      f = x[0];
      double gT = Math.sin(0.5 * Math.PI * time);
      //for(i=1;i<NV;i++){g+=pow((x[j]-sin(0.5*PI*t)),2);}
      for (int i = 1; i < numberOfVariables; i++) {
        g += Math.pow(x[i] - gT, 2);
      }
      g = g + 1;
      double h = 1 - Math.sqrt(f / g);
      solution.setObjective(0, f);
      solution.setObjective(1, h * g);
    }
  }
   */
}
