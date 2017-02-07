package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.problem.DynamicProblem;

import java.io.Serializable;

/**
 * Cristóbal Barba <cbarba@lcc.uma.es>
 */
public interface FDA extends DynamicProblem<DoubleSolution, FDAUpdateData>,Serializable {
}
