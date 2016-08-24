package org.uma.jmetalsp.application.fda.problem;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.problem.DynamicProblem;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by cris on 19/07/2016.
 */
public class FDA1 extends AbstractDoubleProblem implements DynamicProblem<DoubleSolution, FDAUpdateData>,Serializable {

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
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
    time=1.0d;
    theProblemHasBeenModified=false;
  }
  @Override
  public boolean hasTheProblemBeenModified() {
    //Logger.getGlobal().info("FDA1---hasTeproblemBeenModified -----------------> ");
    return theProblemHasBeenModified;
  }

  @Override
  public void reset() {
    Logger.getGlobal().info("------------------FDA1---RESET -----------------> "+time);
    theProblemHasBeenModified = false ;
  }

  @Override
  public synchronized void  update(FDAUpdateData data) {

    time=data.getTime();
    Logger.getGlobal().info("FDA1---Update -----------------> "+time);
    if(time==0.0){
      time=1.0;
    }
    theProblemHasBeenModified=true;
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];
  //  Logger.getGlobal().info("FDA1---Evaluate ---------------time--> "+time);
    f[0] = solution.getVariableValue(0);
    double g = this.evalG(solution);
    double h = this.evalH(f[0], g);
    f[1] = h * g;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }
  /*public void evaluate(DoubleSolution solution) {
   // Logger.getGlobal().info("FDA1---Evaluate -----------------> ");
    int numberOfVariables = getNumberOfVariables();
    //Logger.getGlobal().info("FDA1---Evaluate --------numberOfVariables---------> " +numberOfVariables);
    int numberOfObjectives = getNumberOfObjectives() ;
    //Logger.getGlobal().info("FDA1---Evaluate -------numberOfObjectives----------> " +numberOfObjectives);
    //if(numberOfObjectives==2) {
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
      //Logger.getGlobal().info("FDA1---Evaluate ---------------time--> "+time);
      //Logger.getGlobal().info("FDA1---Evaluate ---------------f--> "+f);
      //Logger.getGlobal().info("FDA1---Evaluate ---------------h*g--> "+h*g);
   // }
  }*/


  /**
   * Returns the value of the ZDT1 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {

    double gT = Math.sin(0.5 * Math.PI * time);
    double g = 0.0;
    for (int i = 1; i < solution.getNumberOfVariables(); i++) {
      g += Math.pow((solution.getVariableValue(i) - gT), 2);
    }
    //double constant = 9.0 / (solution.getNumberOfVariables() - 1);
    //g = constant * g;
    g = g + 1.0;
    return g;
  }

  /**
   * Returns the value of the ZDT1 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    double h = 1 - Math.sqrt(f / g);
    return h;
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
