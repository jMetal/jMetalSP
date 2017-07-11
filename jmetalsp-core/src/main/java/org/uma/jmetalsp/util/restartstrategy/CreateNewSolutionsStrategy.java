package org.uma.jmetalsp.util.restartstrategy;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.DynamicProblem;

import java.util.List;

/**
 * @author Antonio J. Nebro
 */
public interface CreateNewSolutionsStrategy<S extends Solution<?>> {
  /**
   * Add a number of new solutions to a list of {@link Solution} objects
   * @param solutionList
   * @param problem
   * @param numberOfNewSolutions
   */
  void create(List<S> solutionList, DynamicProblem<S, ?> problem, int numberOfNewSolutions) ;
}
