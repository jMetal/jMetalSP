package org.uma.jmetalsp.updatedata;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.UpdateData;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface AlgorithmResultData extends UpdateData {
	List<? extends Solution<?>> getSolutionList() ;
	double getRunningTime() ;
	int getIterations() ;
}
