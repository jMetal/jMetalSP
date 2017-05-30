package org.uma.jmetalsp.observeddata;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AlgorithmObservedData2 implements ObservedData {
	private List<DoubleSolution> solutionList;

  Map<String, ?> algorithmData ;

	public AlgorithmObservedData2(List<? extends Solution<?>> solutionList, Map<String, ?> algorithmData) {
		this.solutionList = new ArrayList<DoubleSolution>();// solutionList;

    for (Solution aux:solutionList) {
     DoubleSolution ds=(DoubleSolution)aux;
     this.solutionList.add(ds);
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
