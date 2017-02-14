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

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.khaos.perception.core.Observable;
import org.uma.khaos.perception.core.ObservableData;
import org.uma.khaos.perception.core.impl.DefaultObservable;
import org.uma.khaos.perception.core.impl.DefaultObservableData;

import java.util.List;
import java.util.function.Supplier;

/**
 * Class implementing a dynamic version of NSGA-II. Most of the code of the original NSGA-II is
 * reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @todo Explain the behaviour of the dynamic algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicNSGAII<S extends Solution<?>, O extends Observable>
    extends NSGAII<S>
    implements DynamicAlgorithm<List<S>, O> {

  private int completedIterations ;
  private boolean stopAtTheEndOfTheCurrentIteration = false ;

	ObservableData<List<S>> solutionListObservable ;
	ObservableData<Integer> numberOfIterationsObservable;
	O observable ;

  public DynamicNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize,
                       CrossoverOperator<S> crossoverOperator,
                       MutationOperator<S> mutationOperator,
                       SelectionOperator<List<S>, S> selectionOperator,
                       SolutionListEvaluator<S> evaluator,
                       O observable) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);

    completedIterations = 0 ;

	  solutionListObservable = new DefaultObservableData<>() ;
	  numberOfIterationsObservable = new DefaultObservableData<>() ;
	  this.observable = observable ;

	  solutionListObservable = (ObservableData<List<S>>) observable.getObservableData("currentPopulation");
    numberOfIterationsObservable = (ObservableData<Integer>) observable.getObservableData("numberOfIterations");
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

    	solutionListObservable.hasChanged() ;
    	solutionListObservable.notifyObservers(getPopulation());
	    numberOfIterationsObservable.hasChanged() ;
	    numberOfIterationsObservable.notifyObservers(completedIterations);

      restart(100);
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;

      initProgress();
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration ;
  }

  @Override
  public void stopTheAlgorithm() {
    stopAtTheEndOfTheCurrentIteration = true ;
  }

  @Override protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart(100);

      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
      getDynamicProblem().reset();
    }
    evaluations += getMaxPopulationSize() ;
    completedIterations ++ ;
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
  public O getObservable() {
    return this.observable ;
  }

  @Override
  public void restart(int percentageOfSolutionsToRemove) {
    SolutionListUtils.restart(getPopulation(), getDynamicProblem(), percentageOfSolutionsToRemove);
  }
}
