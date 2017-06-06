package org.uma.jmetalsp.util.restartstrategy.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.util.restartstrategy.CreateNewSolutionsStrategy;

import java.util.List;

import static org.uma.jmetal.util.SolutionListUtils.fillPopulationWithNewSolutions;

/**
 * Created by antonio on 6/06/17.
 */
public class CreateNRandomSolutions<S extends Solution<?>> implements CreateNewSolutionsStrategy<S> {
  private int percentageOfNewSolutionsToCreate ;

  public CreateNRandomSolutions(int percentageOfNewSolutionsToCreate) {
    this.percentageOfNewSolutionsToCreate = percentageOfNewSolutionsToCreate ;
  }

  @Override
  public void create(List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }

    fillPopulationWithNewSolutions(solutionList, problem, percentageOfNewSolutionsToCreate) ;
  }
}
