package org.uma.jmetalsp.util.restartstrategy;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.DynamicProblem;

import java.util.List;

/**
 * Created by antonio on 6/06/17.
 */
public interface RemoveSolutionsStrategy<S extends Solution<?>> {
  void remove(List<S> solutionList, DynamicProblem<S, ?> problem) ;
}
