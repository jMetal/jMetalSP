package org.uma.jmetalsp.util.restartstrategy.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.util.restartstrategy.RemoveSolutionsStrategy;

import java.util.List;

import static org.uma.jmetal.util.SolutionListUtils.removeSolutionsFromList;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveFirstNSolutions<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int percentageOfSolutionsToDelete ;

  public RemoveFirstNSolutions(int percentageOfSolutionsToDelete) {
    this.percentageOfSolutionsToDelete = percentageOfSolutionsToDelete ;
  }

  @Override
  public void remove(List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }

    removeSolutionsFromList(solutionList, (int)(percentageOfSolutionsToDelete/100.0 * solutionList.size()));
  }
}
