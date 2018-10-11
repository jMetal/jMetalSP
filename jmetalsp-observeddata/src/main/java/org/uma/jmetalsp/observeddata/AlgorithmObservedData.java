package org.uma.jmetalsp.observeddata;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlgorithmObservedData implements ObservedData<Map<String, Object> > {

  Map<String, Object> algorithmData ;

  public AlgorithmObservedData() {
    algorithmData = null ;
  }

  public AlgorithmObservedData(Map<String, Object> map) {
    this.algorithmData = map ;
  }

  public AlgorithmObservedData(List<Solution<?>> solutionList, Map<String, Object> algorithmData) {
    List<ObservedSolution> newSolutionList = new ArrayList<>();

    for (Solution<?> solution:solutionList) {
      newSolutionList.add(new ObservedSolution(solution));
    }

    algorithmData.put("solutionList", newSolutionList) ;
    this.algorithmData = algorithmData ;
  }
  @Override
  public Map<String, Object> getData() {
    return algorithmData;
  }

  @Override
  public String toJson() {
    return JsonWriter.objectToJson(this);
  }

  @Override
  public AlgorithmObservedData fromJson(String jsonString) {

    return (AlgorithmObservedData)JsonReader.jsonToJava(jsonString);
  }


}

