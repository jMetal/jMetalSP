package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF5 extends DF implements Serializable {
    public DF5(Observable<ObservedValue<Integer>> observable){
        this(10,2, observable);
    }

    public DF5() {
        this(new DefaultObservable<>()) ;
    }

    public DF5(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF5");

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
        double G = Math.sin(0.5*Math.PI*time);
        int w = (int)Math.floor(10*G);
        double g = 1.0d+helperSum(solution,1,solution.getNumberOfVariables(),G);

        f[0] = g*(solution.getVariableValue(0)+0.02*Math.sin(w*Math.PI*solution.getVariableValue(0)))+2*time;
        f[1] = g*(1-solution.getVariableValue(0)+0.02*Math.sin(w*Math.PI*solution.getVariableValue(0)))+2*time;

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }

}
