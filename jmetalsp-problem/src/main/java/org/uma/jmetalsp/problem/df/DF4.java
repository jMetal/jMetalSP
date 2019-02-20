package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF4 extends DF implements Serializable {
    public DF4(Observable<ObservedValue<Integer>> observable){
        this(10,2, observable);
    }

    public DF4() {
        this(new DefaultObservable<>()) ;
    }

    public DF4(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF4");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-2.0);
            upperLimit.add(2.0);
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
        double a = Math.sin(0.5*Math.PI*time);
        double b = 1.0d+ Math.abs(Math.cos(0.5*Math.PI*time));
        double c = Math.max(Math.abs(a),a+b);
        double H = 1.5+a;
        double g = 1.0+evaluateG(solution, a,c);


        f[0] = g*Math.pow(Math.abs(solution.getVariableValue(0)-a),H);
        f[1] = g*Math.pow(Math.abs(solution.getVariableValue(0)-a-b),H);
       //if (Double.isNaN(f[1])){
       //     f[1]=1.0;
       //}

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }

    private double evaluateG(DoubleSolution solution, double a, double c){
        double result=0.0;
        for (int i=1;i<solution.getNumberOfVariables();i++){
            result+=Math.pow(solution.getVariableValue(i)-a*Math.pow(solution.getVariableValue(0)/c,2)/(i+1),2);
        }
        return result;
    }
}
