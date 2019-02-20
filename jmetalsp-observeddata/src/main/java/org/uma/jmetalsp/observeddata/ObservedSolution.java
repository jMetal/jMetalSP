package org.uma.jmetalsp.observeddata;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.point.impl.ArrayPoint;
//import org.uma.jmetal.util.point.util.PointSolution;
import org.uma.jmetalsp.ObservedData;

import java.util.ArrayList;
import java.util.List;

public class ObservedSolution<T, S extends Solution<T>> implements ObservedData<S> {

  private List<T> variables ;
  private List<Double> objectives ;
  private S solution;
  public ObservedSolution() {
    solution = null;
  }

  public ObservedSolution(S solution) {
    this.solution = solution;
    variables = new ArrayList<>() ;
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      variables.add((T)solution.getVariableValue(i)) ;
    }

    objectives = new ArrayList<>() ;
    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      objectives.add(solution.getObjective(i)) ;
    }
  }

  public List<T> getVariables() {
    return variables ;
  }

  public List<Double> getObjectives() {
    return objectives ;
  }

  public PointSolution getPointSolution() {
    double[] values = new double[objectives.size()] ;
    for (int i = 0; i < objectives.size(); i++) {
      values[i] = objectives.get(i) ;
    }

    Point point = new ArrayPoint(values) ;
    return new PointSolution(point) ;
  }

  public String toString() {
    String result = "" ;
    for (Double objective : objectives) {
      result += objective + ", " ;
    }

    return result ;
  }

  public S getSolution() {
    return solution;
  }

  @Override
  public String toJson() {
    return JsonWriter.objectToJson(this);
  }

  @Override
  public ObservedSolution<T, S> fromJson(String jsonString) {
    return (ObservedSolution<T, S>) JsonReader.jsonToJava(jsonString);
  }


  @Override
  public S getData() {
    return solution;
  }
}
