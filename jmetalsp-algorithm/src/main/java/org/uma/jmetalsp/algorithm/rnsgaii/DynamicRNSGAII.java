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

package org.uma.jmetalsp.algorithm.rnsgaii;

import org.uma.jmetal.algorithm.multiobjective.rnsgaii.RNSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;

import java.util.*;

/**
 * Class implementing a dynamic version of NSGA-II. Most of the code of the original NSGA-II is
 * reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @todo Explain the behaviour of the dynamic algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicRNSGAII<S extends Solution<?>>
    extends RNSGAII<S>
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData<S>>,
        Observer<SingleObservedData<List<Double>>> {

  private int completedIterations ;
  private boolean stopAtTheEndOfTheCurrentIteration = false ;
  private RestartStrategy<S> restartStrategyForProblemChange ;
  private RestartStrategy<S> restartStrategyForReferencePointChange ;
  Observable<AlgorithmObservedData<S>> observable ;
  private Map<String,List> algorithmData;
  private Optional<List<S>> newReferencePoint ;

  public DynamicRNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize,
                        CrossoverOperator<S> crossoverOperator,
                        MutationOperator<S> mutationOperator,
                        SelectionOperator<List<S>, S> selectionOperator,
                        SolutionListEvaluator<S> evaluator,
                        Observable<AlgorithmObservedData<S>> observable,List<Double> referencePoint, double epsilon) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator,referencePoint,epsilon);
    this.newReferencePoint = Optional.ofNullable(null);
    this.completedIterations = 0 ;
    this.observable = observable ;
    this.restartStrategyForProblemChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<S>(populationSize),
            new CreateNRandomSolutions<S>()) ;
    this.restartStrategyForReferencePointChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<S>(populationSize),
            new CreateNRandomSolutions<S>()) ;
    this.algorithmData = new HashMap<>();

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
      //getPopulation
      observable.notifyObservers(new AlgorithmObservedData<S>(getResult(), algorithmData));

      restart();
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;

      initProgress();
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration ;
  }


  @Override protected void updateProgress() {
    /*if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart();

      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
      getDynamicProblem().reset();
    }
    evaluations += getMaxPopulationSize() ;*/
    if (newReferencePoint.isPresent()) {
      this.updateNewReferencePoint(newReferencePoint.get());
      this.restartStrategyForReferencePointChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
      restart() ;
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
      newReferencePoint = Optional.ofNullable(null);
      evaluations = 0 ;
    } else if (getDynamicProblem().hasTheProblemBeenModified()) {
      this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
      restart() ;
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
      getDynamicProblem().reset();
      evaluations = 0 ;
    } else {
      evaluations += getMaxPopulationSize() ;
    }

  }

  @Override
  public String getName() {
    return "Dynamic R-NSGA-II";
  }

  @Override
  public String getDescription() {
    return "Dynamic version of algorithm R-NSGA-II";
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
  public void updateNewReferencePoint(List<S> newReferencePoints) {
    List<Double> referencePoint = new ArrayList<>();
            //Arrays.asList(
            //newReferencePoint.getObjective(0),
            //newReferencePoint.getObjective(1)) ;
    for (S point:newReferencePoints) {
      for (int i = 0; i < point.getNumberOfObjectives(); i++) {
        referencePoint.add(point.getObjective(i));
      }
    }

    super.updatePointOfInterest(referencePoint);
    algorithmData.put("referencePoint",referencePoint);
    List<S> emptyList = new ArrayList<>();
    observable.setChanged();
    observable.notifyObservers(new AlgorithmObservedData(emptyList, algorithmData));
  }
  public void setRestartStrategyForReferencePointChange(RestartStrategy<S> restartStrategyForReferencePointChange) {
    this.restartStrategyForReferencePointChange = restartStrategyForReferencePointChange ;
  }

  @Override
  public synchronized void update(
          Observable<SingleObservedData<List<Double>>> observable,
          SingleObservedData<List<Double>> data) {
    //if (data.getData().size() != getDynamicProblem().getNumberOfObjectives()) {
    ///  throw new JMetalException("The reference point size is not correct: " + data.getData().size()) ;
    //}
    List<S> newReferences = new ArrayList<>();

    int numberOfPoints = data.getData().size()/getDynamicProblem().getNumberOfObjectives();
     int index = 0;
    for (int i = 0; i < numberOfPoints ; i++) {
      S solution = getDynamicProblem().createSolution();
      for (int j = 0; j < getDynamicProblem().getNumberOfObjectives(); j++) {
        solution.setObjective(j, data.getData().get(index));
        index++;
      }
      newReferences.add(solution);

    }
    newReferencePoint = Optional.of(newReferences) ;

  }


}
