package org.uma.jmetalsp.util.restartstrategy;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.DynamicProblem;

import java.util.List;

/**
 * Created by antonio on 6/06/17.
 */
public interface CreateNewSolutionsStrategy<S extends Solution<?>> {
  void create(List<S> solutionList, DynamicProblem<S, ?> problem) ;
}
