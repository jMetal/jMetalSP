package org.uma.jmetalsp.util.restartstrategy.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;

import java.util.List;

import static org.uma.jmetal.util.SolutionListUtils.fillPopulationWithNewSolutions;
import static org.uma.jmetal.util.SolutionListUtils.removeSolutionsFromList;

/**
 * @author Antonio J. Nebro
 */
public class RestartRemovingTheFirstNSolutions<S extends Solution<?>> implements RestartStrategy<S> {
  private int percentageOfSolutionsToRemove;
  
  public RestartRemovingTheFirstNSolutions(int percentageOfSolutionsToRemove) {
    if ((percentageOfSolutionsToRemove < 0) || (percentageOfSolutionsToRemove > 100)) {
      throw new JMetalException("The percentage of solutions to remove is invalid: " + percentageOfSolutionsToRemove) ;
    }
    this.percentageOfSolutionsToRemove = percentageOfSolutionsToRemove ;
  }

  @Override
  public void restart(List<S> solutionList, DynamicProblem<S,?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }

    int solutionListOriginalSize = solutionList.size() ;
    int numberOfSolutionsToRemove = (int)(solutionListOriginalSize * percentageOfSolutionsToRemove / 100.0) ;

    removeSolutionsFromList(solutionList, numberOfSolutionsToRemove);
    fillPopulationWithNewSolutions(solutionList, problem, solutionListOriginalSize);
  }
}
