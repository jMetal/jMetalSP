package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDA5 extends FDA implements Serializable {

  private boolean theProblemHasBeenModified;

  private final int M = 3;

  public FDA5(Observable<ObservedValue<Integer>> observable){
    this(12,3, observable);
  }

  public FDA5() {
    this(new DefaultObservable<>()) ;
  }

  public FDA5(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
	  super(observer) ;
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA5");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = 0; i < getNumberOfVariables(); i++) {
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
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];
    double g = this.evalG(solution, M-1);
    double Ft= 1.0d+ 100.0d *Math.pow(Math.sin(0.5d*Math.PI*time),4.0d);
    f[0] = this.evalF1(solution, g,Ft);
    f[1] = evalFK(solution,g,2,Ft);
    f[2] = evalFM(solution,g,Ft);
    for (int i = 0; i < solution.getNumberOfObjectives() ; i++) {
      solution.setObjective(i,f[i]);
    }
  }

  private double evalF1(DoubleSolution solution,double g,double Ft){
    double f=1.0d +g;
    double mult=1.0d;
    for (int i = 1; i <= M-1; i++) {
      double y_i= Math.pow(solution.getVariableValue(i-1),Ft);
      mult*=Math.cos(y_i*Math.PI/2.0d);
    }
    return f*mult;
  }


  private double evalFK(DoubleSolution solution,double g,int k,double Ft){
    double f= 1.0d + g;
    double mult=1.0d;
    for (int i = 1; i <= M-k; i++) {
      double y_i= Math.pow(solution.getVariableValue(i-1),Ft);
      mult*=Math.cos(y_i*Math.PI/2.0d);
    }
    double yy = Math.pow(solution.getVariableValue(M-k),Ft);
    mult*= Math.sin(yy*Math.PI/2.0d);
    return f*mult;
  }


  /**
   * Returns the value of the FDA5 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution,int limitInf) {
    double g = 0.0d;
    double Gt=Math.abs(Math.sin(0.5d*Math.PI*time));
    for (int i = limitInf; i < solution.getNumberOfVariables(); i++) {
      g += Math.pow((solution.getVariableValue(i)-Gt), 2.0d);
    }
    return g+Gt;
  }

  private double evalFM(DoubleSolution solution,double g,double Ft){
    double fm = 1.0d+g;
    double y_1= Math.pow(solution.getVariableValue(0),Ft);
    double mult= Math.sin(y_1*Math.PI/2.0d);
    return fm*mult;
  }
}
