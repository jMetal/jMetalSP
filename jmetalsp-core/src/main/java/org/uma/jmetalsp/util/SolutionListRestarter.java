//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalsp.util;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.List;

/**
 * This class contains util methods to re-starting a solution list
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SolutionListRestarter<S extends Solution<?>>{

  /**
   * This methods takes a list of solutions, removes a percentage of its solutions, and it is filled
   * with new random generated solutions
   * @param solutionList
   * @param problem
   * @param percentageOfSolutionsToRemove
   */
  public void restart(List<S> solutionList, Problem<S> problem, int percentageOfSolutionsToRemove) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (problem == null) {
      throw new JMetalException("The problem is null") ;
    } else if ((percentageOfSolutionsToRemove < 0) || (percentageOfSolutionsToRemove > 100)) {
      throw new JMetalException("The percentage of solutions to remove is invalid: " + percentageOfSolutionsToRemove) ;
    }

    int solutionListOriginalSize = solutionList.size() ;
    int numberOfSolutionsToRemove = (int)(solutionListOriginalSize * percentageOfSolutionsToRemove / 100.0) ;

    removeSolutionsFromList(solutionList, numberOfSolutionsToRemove);
    fillPopulationWithNewSolutions(solutionList, problem, solutionListOriginalSize);
  }

  /**
   * Removes a number of solutions from a list
   * @param solutionList
   * @param numberOfSolutionsToRemove
   */
  public void removeSolutionsFromList(List<S> solutionList, int numberOfSolutionsToRemove) {
    if (solutionList.size() < numberOfSolutionsToRemove) {
      throw new JMetalException("The list size (" + solutionList.size()+") is lower than " +
          "the number of solutions to remove ("+numberOfSolutionsToRemove+")") ;
    }

    for (int i = 0 ; i < numberOfSolutionsToRemove; i++) {
      solutionList.remove(0) ;
    }
  }

  public void fillPopulationWithNewSolutions(List<S> solutionList, Problem<S> problem, int maxListSize) {
    while (solutionList.size() < maxListSize) {
      solutionList.add(problem.createSolution());
    }
  }
}
