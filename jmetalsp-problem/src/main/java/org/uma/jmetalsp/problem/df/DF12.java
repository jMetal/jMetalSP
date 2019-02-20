package org.uma.jmetalsp.problem.df;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

public class DF12 extends DF implements Serializable {
    public DF12(Observable<ObservedValue<Integer>> observable){
        this(10,3, observable);
    }

    public DF12() {
        this(new DefaultObservable<>()) ;
    }

    public DF12(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF12");

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

        double k = 10.0d*Math.sin(Math.PI*time);
        double g = 1.0d+helperSum(solution,2,solution.getNumberOfVariables(),
            Math.sin(time*solution.getVariableValue(0)));
        g+= Math.abs(Math.sin(Math.floor(k*(2.0d*solution.getVariableValue(0)-1.0d))*Math.PI/2)*
            Math.sin(Math.floor(k*(2*solution.getVariableValue(1)-1.0d))*Math.PI/2));

        f[0] = g*Math.cos(0.5*Math.PI*solution.getVariableValue(1))*Math.cos(0.5*Math.PI*solution.getVariableValue(0));
        f[1] = g*Math.sin(0.5*Math.PI*solution.getVariableValue(1))*Math.cos(0.5*Math.PI*solution.getVariableValue(0));
        f[2] = g*Math.sin(0.5*Math.PI*solution.getVariableValue(0));
        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
        solution.setObjective(2, f[2]);
    }


}
