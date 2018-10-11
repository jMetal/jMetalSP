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
public class FDA1 extends FDA implements Serializable {

  public FDA1(Observable<ObservedValue<Integer>> observable){
    this(100,2, observable);
  }

  public FDA1() {
    this(new DefaultObservable<>()) ;
  }

  public FDA1 (Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
  	super(observer) ;
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
    return theProblemHasBeenModified;
  }

  @Override
  public void reset() {
    theProblemHasBeenModified = false ;
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];
    f[0] = solution.getVariableValue(0);
    double g = this.evalG(solution);
    double h = this.evalH(f[0], g);
    f[1] = h * g;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the FDA1 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {

    double gT = Math.sin(0.5 * Math.PI * time);
    double g = 0.0;
    for (int i = 1; i < solution.getNumberOfVariables(); i++) {
      g += Math.pow((solution.getVariableValue(i) - gT), 2);
    }
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
}
