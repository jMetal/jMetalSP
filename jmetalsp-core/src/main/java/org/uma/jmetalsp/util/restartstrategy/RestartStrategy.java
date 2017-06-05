package org.uma.jmetalsp.util.restartstrategy;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.DynamicProblem;

import java.util.List;

/**
 * Interface describing restart strategies to apply when changes (in the problem or the reference point) have been
 * detected
 *
 * @author Antonio J. Nebro
 */
public interface RestartStrategy<S extends Solution<?>> {
  void restart(List<S> solutionList, DynamicProblem<S, ?> problem) ;
}
