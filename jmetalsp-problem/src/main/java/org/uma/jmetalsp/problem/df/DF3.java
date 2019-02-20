package org.uma.jmetalsp.problem.df;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DF3 extends DF implements Serializable {
    public DF3(Observable<ObservedValue<Integer>> observable){
        this(10,2, observable);
    }

    public DF3() {
        this(new DefaultObservable<>()) ;
    }

    public DF3(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF3");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        lowerLimit.add(0.0);
        upperLimit.add(1.0);
        for (int i = 1; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-1.0);
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
        double G = Math.sin(0.5*Math.PI*time);
        double H = G+1.5;
        double y = G+(double)Math.pow((double)solution.getVariableValue(0),(double)H);
        //if(Double.isNaN(y)){
        //    y= Double.MAX_VALUE;
        //}
        double g = 1.0+helperSum(solution,1,solution.getNumberOfVariables(),
                y);

        f[0] = solution.getVariableValue(0);
        f[1] = (double) g*(1.0-(double)Math.pow(solution.getVariableValue(0)/g,H));
       //if (Double.isNaN(f[1])){
       //     f[1]=1.0;
       //}

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }

}
