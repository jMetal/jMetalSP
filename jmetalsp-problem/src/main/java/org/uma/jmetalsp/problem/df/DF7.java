package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF7 extends DF implements Serializable {
    public DF7(Observable<ObservedValue<Integer>> observable){
        this(10,2, observable);
    }

    public DF7() {
        this(new DefaultObservable<>()) ;
    }

    public DF7(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF7");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        lowerLimit.add(1.0);
        upperLimit.add(4.0);
        for (int i = 1; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0.0);
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
        double a = 5.0d*Math.cos(0.5d*Math.PI*time);
        double tmp=1.0d/(1.0d+Math.exp(a*(solution.getVariableValue(0)-2.5d)));

        double g = 1+helperSum(solution,1,solution.getNumberOfVariables(),tmp);

        f[0] = (g*(1.0+time)/solution.getVariableValue(0))+2*time;
        f[1] = (g*solution.getVariableValue(0)/(1+time))+2*time;

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }


}
