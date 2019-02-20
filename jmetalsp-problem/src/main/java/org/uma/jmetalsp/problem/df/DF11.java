package org.uma.jmetalsp.problem.df;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

public class DF11 extends DF implements Serializable {
    public DF11(Observable<ObservedValue<Integer>> observable){
        this(10,3, observable);
    }

    public DF11() {
        this(new DefaultObservable<>()) ;
    }

    public DF11(Integer numberOfVariables, Integer numberOfObjectives, Observable<ObservedValue<Integer>> observer) throws JMetalException {
        super(observer) ;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("DF11");

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
        double G = Math.sin(0.5d*Math.PI*time);
        double g = 1.0d+G+helperSum(solution,2,solution.getNumberOfVariables(),
            0.5*G*solution.getVariableValue(0));

        double y1 = Math.PI*G/6+(Math.PI/2-Math.PI*G/3)*solution.getVariableValue(0);
        double y2 = Math.PI*G/6+(Math.PI/2-Math.PI*G/3)*solution.getVariableValue(1);
        f[0] = g*Math.sin(y1);
        f[1] = g*Math.sin(y2)*Math.cos(y1);
        f[2] = g*Math.cos(y2)*Math.cos(y1);
        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
        solution.setObjective(2, f[2]);
    }


}
