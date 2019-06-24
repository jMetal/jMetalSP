package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF8 extends DF implements Serializable {
    public DF8(Observable<ObservedValue<Integer>> observable){
        this(10,2, observable);
    }

    public DF8() {
        this(new DefaultObservable<>()) ;
    }

    public DF8(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF8");

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
        time=0.0d;
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
        double G = Math.sin(0.5d*Math.PI*time);
        double a = 2.25d+2.0d*Math.cos(2.0d*Math.PI*time);
        double b = 100.d*Math.pow(G,2);
        double tmp=G*Math.sin(4.0d*Math.PI*Math.pow(solution.getVariableValue(0),b))/(1+Math.abs(G));
        double g = 1+helperSum(solution,1,solution.getNumberOfVariables(),tmp);

        f[0] = (g*(solution.getVariableValue(0)+0.1d*Math.sin(3.0d*Math.PI*solution.getVariableValue(0))))+4*time;
        f[1] = (g*Math.pow(1.0d-solution.getVariableValue(0)+0.1d*Math.sin(3.0d*Math.PI*solution.getVariableValue(0)),a))+4*time;

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }


}
