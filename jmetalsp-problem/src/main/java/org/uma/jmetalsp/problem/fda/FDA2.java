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
public class FDA2 extends FDA implements Serializable {

  private boolean theProblemHasBeenModified;

  public FDA2(Observable<ObservedValue<Integer>> observable){
    this(31,2, observable);
  }

  public FDA2() {
    this(new DefaultObservable<>()) ;
  }

  public FDA2 (Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
    super(observer) ;
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA2");

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
    double g = this.evalG(solution,1,(solution.getNumberOfVariables()/2)+1);
    double h = this.evalH(f[0], g);
    f[1] = g*h;//1-Math.sqrt(f[0]);
    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the FDA2 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution,int limitInf,int limitSup) {

    double g = 0.0;
    for (int i = limitInf; i < limitSup; i++) {
      g += Math.pow(solution.getVariableValue(i), 2.0);
    }
    for (int i = limitSup; i < solution.getNumberOfVariables(); i++) {
      g += Math.pow((solution.getVariableValue(i) +1.0), 2.0);
    }
    g = g + 1.0;
    return g;
  }

  /**
   * Returns the value of the FDA function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  private  double evalH(double f, double g) {
    double HT= 0.2 + 4.8*Math.pow(time,2.0);
    double h = 1.0 - Math.pow((f / g),HT);
    return h;
  }
}
