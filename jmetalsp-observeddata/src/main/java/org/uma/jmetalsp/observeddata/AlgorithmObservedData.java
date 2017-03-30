package org.uma.jmetalsp.observeddata;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AlgorithmObservedData implements ObservedData {
	private List<? extends Solution<?>> solutionList;
	private int numberOfIterations;

	public AlgorithmObservedData(List<? extends Solution<?>> solutionList, int numberOfIterations) {
		this.solutionList = solutionList;
		this.numberOfIterations = numberOfIterations;
	}

	public List<? extends Solution<?>> getSolutionList() {
		return solutionList;
	}

	public int getIterations() {
		return numberOfIterations;
	}
}
