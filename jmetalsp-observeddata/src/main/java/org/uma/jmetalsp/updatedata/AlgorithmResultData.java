package org.uma.jmetalsp.updatedata;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface AlgorithmResultData extends ObservedData {
	List<? extends Solution<?>> getSolutionList() ;
	double getRunningTime() ;
	int getIterations() ;
}
