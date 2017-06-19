package org.uma.jmetalsp.observeddata;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AlgorithmObservedData2<S extends Solution<?>> implements ObservedData {
	private List<S> solutionList;

  Map<String, ?> algorithmData ;

	public AlgorithmObservedData2(List<S> solutionList, Map<String, ?> algorithmData) {
		this.solutionList = new ArrayList<>();

    for (S solution:solutionList) {
     this.solutionList.add(solution);
    }
    this.algorithmData = algorithmData ;
	}

	public List<? extends Solution<?>> getSolutionList() {
		return solutionList;
	}

  public Map<String, ?> getAlgorithmData() {
    return algorithmData;
  }

}
