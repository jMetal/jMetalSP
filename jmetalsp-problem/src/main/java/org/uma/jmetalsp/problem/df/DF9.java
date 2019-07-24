package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF9 extends DF implements Serializable {
    public DF9(Observable<ObservedValue<Integer>> observable){
        this(10,2, observable);
    }

    public DF9() {
        this(new DefaultObservable<>()) ;
    }

    public DF9(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF9");

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
        double N= 1.d+ Math.floor(10.d*Math.abs(Math.sin(0.5d*Math.PI*time)));
        double g=1.0d+evaluateG(solution);
        f[0] = (g*(solution.getVariableValue(0)+
                Math.max(0,(0.1+0.5/N)*Math.sin(2*N*Math.PI*solution.getVariableValue(0)))))+2*time;
        f[1] = (g*(1.0d-solution.getVariableValue(0)+
                Math.max(0,(0.1+0.5/N)*Math.sin(2*N*Math.PI*solution.getVariableValue(0)))))+2*time;

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }
    private double evaluateG(DoubleSolution solution){
        double result=0.0;
        for (int i=1;i<solution.getNumberOfVariables();i++){
            double tmp = solution.getVariableValue(i)-
                    Math.cos(4.0d*time+solution.getVariableValue(0)+solution.getVariableValue(i-1));
            result+=Math.pow(tmp,2);
        }
        return result;
    }

}
