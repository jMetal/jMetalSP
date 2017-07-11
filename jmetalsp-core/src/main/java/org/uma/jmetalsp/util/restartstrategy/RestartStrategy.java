package org.uma.jmetalsp.util.restartstrategy;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.DynamicProblem;

import java.util.List;

/**
 * Created by antonio on 6/06/17.
 */
public class RestartStrategy<S extends Solution<?>> {

  private final RemoveSolutionsStrategy<S> removeSolutionsStrategy;
  private final CreateNewSolutionsStrategy<S> createNewSolutionsStrategy;

  public RestartStrategy(RemoveSolutionsStrategy<S> removeSolutionsStrategy,
                         CreateNewSolutionsStrategy<S> createNewSolutionsStrategy) {
    this.removeSolutionsStrategy = removeSolutionsStrategy ;
    this.createNewSolutionsStrategy = createNewSolutionsStrategy ;
  }

  public void restart(List<S> solutionList, DynamicProblem<S,?> problem) {
    int numberOfRemovedSolutions = removeSolutionsStrategy.remove(solutionList, problem);
    createNewSolutionsStrategy.create(solutionList, problem, numberOfRemovedSolutions); ;
  }
}
