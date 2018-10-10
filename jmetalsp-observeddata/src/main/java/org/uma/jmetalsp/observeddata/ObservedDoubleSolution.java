package org.uma.jmetalsp.observeddata;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.ObservedData;

import java.util.ArrayList;
import java.util.List;

public class ObservedDoubleSolution implements ObservedData<List<Double>> {

  private List<Double> variables ;
  private List<Double> objectives ;

  public ObservedDoubleSolution() {
  }

  public ObservedDoubleSolution(DoubleSolution solution) {
    variables = new ArrayList<>() ;
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      variables.add(solution.getVariableValue(i)) ;
    }

    objectives = new ArrayList<>() ;
    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      objectives.add(solution.getObjective(i)) ;
    }
  }

  List<Double> getVariables() {
    return variables ;
  }

  List<Double> getObjectives() {
    return objectives ;
  }

  public String toString() {
    String result = "" ;
    for (Double variable : variables) {
      result += variable + ", " ;
    }

    result += " / " ;

    for (Double objective : objectives) {
      result += objective + ", " ;
    }

    return result ;
  }

  @Override
  public String toJson() {
    return JsonWriter.objectToJson(this) ;
  }

  @Override
  public ObservedDoubleSolution fromJson(String jsonString) {
    return (ObservedDoubleSolution)JsonReader.jsonToJava(jsonString);
  }


  @Override
  public List<Double> getData() {
    return this.objectives;
  }
}
