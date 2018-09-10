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
public class FDA3 extends FDA implements Serializable {

  private boolean theProblemHasBeenModified;
  private final int limitInfI = 0;
  private final int limitSupI = 1;
  private final int limitInfII = 1;

  public FDA3(Observable<ObservedValue<Integer>> observable){
    this(30,2, observable);
  }

  public FDA3() {
    this(new DefaultObservable<>()) ;
  }

  public FDA3 (Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
    super(observer) ;
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA3");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = limitInfI; i < limitSupI; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    for (int i = limitInfII; i < getNumberOfVariables(); i++) {
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
    f[0] = this.evalF(solution, limitInfI, limitSupI);
    double g = this.evalG(solution, limitInfII);
    double h = this.evalH(f[0], g);
    f[1] = g*h;
    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  private double evalF(DoubleSolution solution,int limitInf,int limitSup){
    double f=0.0d;
    double aux = 2.0d*Math.sin(0.5d*Math.PI*time);
    double Ft= Math.pow(10.0d,aux);
    for (int i = limitInf; i < limitSup; i++) {
      f+=Math.pow(solution.getVariableValue(i),Ft);
    }
    return f;
  }


  /**
   * Returns the value of the FDA3 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution,int limitInf) {

    double g = 0.0d;
    double Gt=Math.abs(Math.sin(0.5d*Math.PI*time));
    for (int i = limitInf; i < solution.getNumberOfVariables(); i++) {
      g += Math.pow((solution.getVariableValue(i)-Gt), 2.0);
    }
    g = g + 1.0 + Gt;
    return g;
  }

  /**
   * Returns the value of the FDA3 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  private  double evalH(double f, double g) {
    double h = 1.0d - Math.sqrt(f / g);
    return h;
  }
}
