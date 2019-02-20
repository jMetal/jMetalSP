package org.uma.jmetalsp.observeddata.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.observeddata.ObservedSolution;

import java.util.List;
import java.util.Map;

public class DummySolution<T> implements Solution<T> {

  private List<T> variables;
  private List<Double> objectives;

  public DummySolution() {
  }

  public DummySolution(ObservedSolution<T, Solution<T>> observedSolution) {
    variables = observedSolution.getVariables();
    objectives = observedSolution.getObjectives();
  }


  @Override
  public void setObjective(int i, double v) {
  }

  @Override
  public double getObjective(int i) {
    return objectives.get(i);
  }

  @Override
  public double[] getObjectives() {
    return new double[0];
  }

  @Override
  public T getVariableValue(int i) {
    return variables.get(i);
  }

  @Override
  public List<T> getVariables() {
    return null;
  }

  @Override
  public void setVariableValue(int i, T t) {

  }

  @Override
  public String getVariableValueString(int i) {
    return variables.get(i).toString();
  }

  @Override
  public int getNumberOfVariables() {
    return variables.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return objectives.size();
  }

  @Override
  public Solution<T> copy() {
    return null;
  }

  @Override
  public void setAttribute(Object o, Object o1) {

  }

  @Override
  public Object getAttribute(Object o) {
    return null;
  }

  @Override
  public Map<Object, Object> getAttributes() {
    return null;
  }
}

