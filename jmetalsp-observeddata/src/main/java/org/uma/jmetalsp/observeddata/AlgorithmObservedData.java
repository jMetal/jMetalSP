package org.uma.jmetalsp.observeddata;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data returned by algorithms to be sent to observers, in the form of a map structure
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AlgorithmObservedData<S extends Solution<?>> implements ObservedData<Map<String, Object>> {
	private List<S> solutionList;

  Map<String, Object> algorithmData ;

	public AlgorithmObservedData(List<S> solutionList, Map<String, Object> algorithmData) {
		this.solutionList = new ArrayList<>();

    for (S solution:solutionList) {
     this.solutionList.add((S) solution.copy());
    }

    algorithmData.put("solutionList", this.solutionList) ;
    this.algorithmData = algorithmData ;
	}

  @Override
  public Map<String, Object> getData() {
    return algorithmData;
  }
}
