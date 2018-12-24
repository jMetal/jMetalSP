package org.uma.jmetalsp.problem.df;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

public class DF13 extends DF implements Serializable {
    public DF13(Observable<ObservedValue<Integer>> observable){
        this(10,3, observable);
    }

    public DF13() {
        this(new DefaultObservable<>()) ;
    }

    public DF13(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF13");

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
        int p = (int) Math.floor(6.0d*G);
        double g = 1.0d+helperSum(solution,2,solution.getNumberOfVariables(), G);


        f[0] = g*Math.cos(0.5*Math.PI*solution.getVariableValue(0));
        f[1] = g*Math.cos(0.5*Math.PI*solution.getVariableValue(1));
        f[2] = g*Math.pow(Math.sin(0.5*Math.PI*solution.getVariableValue(0)),2)+
            Math.sin(0.5*Math.PI*solution.getVariableValue(0))*
                Math.pow(Math.cos(p*Math.PI*solution.getVariableValue(0)),2)+
            Math.pow(Math.sin(0.5* Math.PI*solution.getVariableValue(1)),2)+
            Math.sin(0.5*Math.PI*solution.getVariableValue(1))*
            Math.pow(Math.cos(p*Math.PI*solution.getVariableValue(1)),2);
        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
        solution.setObjective(2, f[2]);
    }


}
