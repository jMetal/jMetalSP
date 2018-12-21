package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF10 extends DF implements Serializable {
    public DF10(Observable<ObservedValue<Integer>> observable){
        this(10,3, observable);
    }

    public DF10() {
        this(new DefaultObservable<>()) ;
    }

    public DF10(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF10");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        lowerLimit.add(0.0);
        upperLimit.add(1.0);
        lowerLimit.add(0.0);
        upperLimit.add(1.0);
        for (int i = 2; i < getNumberOfVariables(); i++) {
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
        double H = 2.25d+2.0d*Math.cos(0.5d*Math.PI*time);

        double tmp=Math.sin(2.0d*Math.PI*(solution.getVariableValue(0)+solution.getVariableValue(1)))/(1+Math.abs(G));
        double g = 1+helperSum(solution,2,solution.getNumberOfVariables(),tmp);

        f[0] = g*Math.pow(Math.sin(0.5*Math.PI*solution.getVariableValue(0)),H);
        f[1] = g*Math.pow(Math.sin(0.5*Math.PI*solution.getVariableValue(1))*Math.cos(0.5*Math.PI*solution.getVariableValue(0)),H);
        f[2] = g*Math.pow(Math.cos(0.5*Math.PI*solution.getVariableValue(1))*Math.cos(0.5* Math.PI*solution.getVariableValue(0)),H);
        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
        solution.setObjective(2, f[2]);
    }


}
