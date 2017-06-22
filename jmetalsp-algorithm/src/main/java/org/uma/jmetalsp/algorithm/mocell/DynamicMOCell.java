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

package org.uma.jmetalsp.algorithm.mocell;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.solutionattribute.impl.LocationAttribute;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class implementing a dynamic version of MOCell. Most of the code of the original MOCell algorithm
 * is reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @todo Explain the behaviour of the dynamic algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicMOCell<S extends Solution<?>, O extends Observable<AlgorithmObservedData>>
    extends MOCell<S>
    implements DynamicAlgorithm<List<S>, Observable<AlgorithmObservedData>> {

  private int completedIterations ;
  private boolean stopAtTheEndOfTheCurrentIteration = false ;
  private O observable ;
  private Map<String,List> algorithmData;

  public DynamicMOCell(DynamicProblem<S, ?> problem,
                       int maxEvaluations,
                       int populationSize,
                       BoundedArchive<S> archive,
                       Neighborhood<S> neighborhood,
                       CrossoverOperator<S> crossoverOperator,
                       MutationOperator<S> mutationOperator,
                       SelectionOperator<List<S>, S> selectionOperator,
                       SolutionListEvaluator<S> evaluator,
                       O observable) {
    super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator,
            selectionOperator, evaluator);

    completedIterations = 0 ;
    this.observable = observable ;
    this.algorithmData = new HashMap<>();
  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return (DynamicProblem<S, ?>) super.getProblem();
  }

  @Override
  public int getCompletedIterations() {
    return completedIterations ;
  }

  @Override protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {
      observable.setChanged() ;
      List<Integer> data= new ArrayList<>();
      data.add(completedIterations);
      algorithmData.put("numberOfIterations",data);
      observable.notifyObservers(new AlgorithmObservedData(getPopulation(), algorithmData));
      restart();
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration;
  }

  @Override
  public void stopTheAlgorithm() {
    stopAtTheEndOfTheCurrentIteration = true ;
  }

  @Override
  public void restart() {
    SolutionListUtils.restart(getPopulation(), getDynamicProblem(), 100);
    location = new LocationAttribute<>(getPopulation());
    evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
    initProgress();
  }

  @Override protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart();
      getDynamicProblem().reset();
    }
    evaluations ++ ;
    currentIndividual=(currentIndividual+1)%getMaxPopulationSize();
  }

  @Override
  public String getName() {
    return "DynamicMOCell";
  }

  @Override
  public String getDescription() {
    return "Dynamic version of algorithm MOCell";
  }

  @Override
  public O getObservable() {
    return this.observable ;
  }
}
