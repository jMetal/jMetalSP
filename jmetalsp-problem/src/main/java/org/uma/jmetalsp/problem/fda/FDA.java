package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.problem.DynamicProblem;

import java.io.Serializable;

/**
 * Created by cris on 09/01/2017.
 */
public interface FDA extends DynamicProblem<DoubleSolution, FDAUpdateData>,Serializable {
}
