package org.uma.jmetalsp.observeddata;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AlgorithmObservedData2 implements ObservedData {
	private List<? extends Solution<?>> solutionList;

  Map<String, ?> algorithmData ;

	public AlgorithmObservedData2(List<? extends Solution<?>> solutionList, Map<String, ?> algorithmData) {
		this.solutionList = solutionList;
		this.algorithmData = algorithmData ;
	}

	public List<? extends Solution<?>> getSolutionList() {
		return solutionList;
	}

  public Map<String, ?> getAlgorithmData() {
    return algorithmData;
  }

}
