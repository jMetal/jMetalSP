package org.uma.jmetalsp.util.restartstrategy.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.util.restartstrategy.RemoveSolutionsStrategy;

import java.util.List;

import static org.uma.jmetal.util.SolutionListUtils.removeSolutionsFromList;

/**
 * Created by antonio on 6/06/17.
 */
public class RemoveNSolutionsAccordingToTheCrowdingDistance<S extends Solution<?>> implements RemoveSolutionsStrategy<S> {
  private int percentageOfSolutionsToDelete ;

  public RemoveNSolutionsAccordingToTheCrowdingDistance(int percentageOfSolutionsToDelete) {
    this.percentageOfSolutionsToDelete = percentageOfSolutionsToDelete ;
  }

  @Override
  public void remove(List<S> solutionList, DynamicProblem<S, ?> problem) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    }

    CrowdingDistanceArchive<S> archive = new CrowdingDistanceArchive<>(
            (int)(percentageOfSolutionsToDelete/100.0 * solutionList.size())) ;
    for (S solution: solutionList) {
      archive.add(solution) ;
    }
    solutionList.clear();

    for (S solution: archive.getSolutionList()) {
      solutionList.add(solution) ;
    }
  }
}
