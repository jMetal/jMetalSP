package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF2 extends DF implements Serializable {
    public DF2(Observable<ObservedValue<Integer>> observable){
        this(10,2, observable);
    }

    public DF2() {
        this(new DefaultObservable<>()) ;
    }

    public DF2(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF2");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
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
        double G = Math.abs(Math.sin(0.5*Math.PI*time));
        int r = (int) Math.floor((solution.getNumberOfVariables()-1)*G);
        DoubleSolution copy = (DoubleSolution) solution.copy();
        copy = swap(copy,0,r);
        double g = 1+helperSum(copy,1,copy.getNumberOfVariables(),G);

        f[0] = solution.getVariableValue(0);
        f[1] = g*(1-Math.pow(solution.getVariableValue(0)/g,0.5));

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }

    private DoubleSolution swap(DoubleSolution solution, int pos1, int pos2){
        if(solution.getNumberOfVariables()>pos1 &&
        solution.getNumberOfVariables()>pos2){
            double aux = solution.getVariableValue(pos1);
            solution.setVariableValue(pos1,solution.getVariableValue(pos2));
            solution.setVariableValue(pos2,aux);
        }
        return solution;
    }
}
