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

package org.uma.jmetalsp.algorithm.nsgaii;

import java.util.Comparator;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class implementing a dynamic version of NSGA-II. Most of the code of the original NSGA-II is
 * reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @todo Explain the behaviour of the dynamic algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicNSGAII<S extends Solution<?>>
    extends NSGAII<S>
    implements DynamicAlgorithm<List<S>, AlgorithmObservedData<S>> {

  private int completedIterations ;
  private boolean stopAtTheEndOfTheCurrentIteration = false ;
  private RestartStrategy<S> restartStrategyForProblemChange ;

	Observable<AlgorithmObservedData<S>> observable ;
  public DynamicNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator,
      Observable<AlgorithmObservedData<S>> observable) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator,evaluator);

    completedIterations = 0 ;
    this.observable = observable ;
    this.restartStrategyForProblemChange = new RestartStrategy<>(
        new RemoveFirstNSolutions<S>(populationSize),
        new CreateNRandomSolutions<S>()) ;
  }


  public DynamicNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize,
                       CrossoverOperator<S> crossoverOperator,
                       MutationOperator<S> mutationOperator,
                       SelectionOperator<List<S>, S> selectionOperator,
                       SolutionListEvaluator<S> evaluator,
                       Comparator<S> dominanceComparator,
                       Observable<AlgorithmObservedData<S>> observable) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, dominanceComparator,evaluator);

    completedIterations = 0 ;
    this.observable = observable ;
    this.restartStrategyForProblemChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<S>(populationSize),
            new CreateNRandomSolutions<S>()) ;
  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return (DynamicProblem<S, ?>) super.getProblem();
  }

  @Override protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {
      observable.setChanged() ;

      Map<String, Object> algorithmData = new HashMap<>() ;

      algorithmData.put("numberOfIterations",completedIterations);
      observable.notifyObservers(new AlgorithmObservedData<S>(getPopulation(), algorithmData));

      restart();
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;

      initProgress();
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration ;
  }


  @Override protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart();

      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
      getDynamicProblem().reset();
    }
    evaluations += getMaxPopulationSize() ;
  }

  @Override
  public String getName() {
    return "DynamicNSGAII";
  }

  @Override
  public String getDescription() {
    return "Dynamic version of algorithm NSGA-II";
  }

  @Override
  public Observable<AlgorithmObservedData<S>> getObservable() {
    return this.observable ;
  }

  @Override
  public void restart() {
    this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>)getProblem());
  }

  @Override
  public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
    this.restartStrategyForProblemChange = (RestartStrategy<S>) restartStrategy;
  }
}
