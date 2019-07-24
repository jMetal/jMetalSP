package org.uma.jmetalsp.problem.df;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

public class DF14 extends DF implements Serializable {
    public DF14(Observable<ObservedValue<Integer>> observable){
        this(10,3, observable);
    }

    public DF14() {
        this(new DefaultObservable<>()) ;
    }

    public DF14(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF14");

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
        double g = 1.0d+helperSum(solution,2,solution.getNumberOfVariables(), G);
        double y = 0.5d+G*(solution.getVariableValue(0)-0.5d);

        f[0] = g*(1-y+0.05d*Math.sin(6.0d*Math.PI*y));
        f[1] = g*(1-solution.getVariableValue(1)+0.05d*Math.sin(6.0d*Math.PI*solution.getVariableValue(1)))*
            (y+0.05d*Math.sin(6.0d*Math.PI*y));
        f[2] = g*(solution.getVariableValue(1)+0.05d*Math.sin(6.0d*Math.PI*solution.getVariableValue(1)))*
            (y+0.05d*Math.sin(6.0d*Math.PI*y));
        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
        solution.setObjective(2, f[2]);
    }


}
