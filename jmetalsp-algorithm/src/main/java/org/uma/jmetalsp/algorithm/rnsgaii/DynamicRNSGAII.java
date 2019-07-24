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
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.DynamicUpdate;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;
import org.uma.jmetalsp.qualityindicator.CoverageFront;
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
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData>,
        Observer<ObservedValue<List<Double>>> {

  private int completedIterations ;
  private boolean stopAtTheEndOfTheCurrentIteration = false ;
  private RestartStrategy<S> restartStrategyForProblemChange ;
  private RestartStrategy<S> restartStrategyForReferencePointChange ;
  private Observable<AlgorithmObservedData> observable ;
  //private Map<String,List> algorithmData;
  private Optional<List<S>> newReferencePoint ;
  private List<S> lastReceivedFront;
  private boolean autoUpdate;
  private CoverageFront<PointSolution> coverageFront;


  public DynamicRNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize,
                        int matingPoolSize, int offspringPopulationSize,
                        CrossoverOperator<S> crossoverOperator,
                        MutationOperator<S> mutationOperator,
                        SelectionOperator<List<S>, S> selectionOperator,
                        SolutionListEvaluator<S> evaluator,
                        Observable<AlgorithmObservedData> observable,List<Double> referencePoint, double epsilon,boolean autoUpdate, CoverageFront<PointSolution> coverageFront) {
    super(problem, maxEvaluations, populationSize, matingPoolSize,offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator,referencePoint,epsilon);
    this.newReferencePoint = Optional.ofNullable(null);
    this.completedIterations = 0 ;
    this.observable = observable ;
    this.autoUpdate = autoUpdate;
    this.coverageFront = coverageFront;
    this.restartStrategyForProblemChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<S>(populationSize),
            new CreateNRandomSolutions<S>()) ;
    this.restartStrategyForReferencePointChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<S>(populationSize),
            new CreateNRandomSolutions<S>()) ;
    //this.algorithmData = new HashMap<>();

  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return (DynamicProblem<S, ?>) super.getProblem();
  }

  @Override protected boolean isStoppingConditionReached() {
    if (evaluations.get() >= maxEvaluations) {
      /*observable.setChanged() ;

      Map<String, Object> algorithmData = new HashMap<>() ;

      algorithmData.put("numberOfIterations",completedIterations);
      algorithmData.put("algorithmName", getName()) ;
      algorithmData.put("problemName", problem.getName()) ;
      algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives()) ;

      observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) getResult(), algorithmData));


      restart();
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;

      initProgress();
      completedIterations++;*/
      boolean coverage = false;
      if (lastReceivedFront != null) {
        Front referenceFront = new ArrayFront(lastReceivedFront);
        coverageFront.updateFront(referenceFront);
        List<PointSolution> pointSolutionList = new ArrayList<>();
        List<S> list = getResult();
        for (S s : list) {
          PointSolution pointSolution = new PointSolution(s);
          pointSolutionList.add(pointSolution);
        }
        coverage = coverageFront.isCoverage(pointSolutionList);

      }

      if (getDynamicProblem() instanceof DynamicUpdate && autoUpdate) {
        ((DynamicUpdate) getDynamicProblem()).update();
      }

      if (coverage) {
        observable.setChanged();
        Map<String, Object> algorithmData = new HashMap<>();
        algorithmData.put("numberOfIterations", completedIterations);
        algorithmData.put("algorithmName", getName());
        algorithmData.put("problemName", problem.getName());
        algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives());


        observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) getResult(), algorithmData));
      }
      lastReceivedFront = getResult();
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
      evaluations.reset();
      //evaluations = 0 ;
    } else if (getDynamicProblem().hasTheProblemBeenModified()) {
      this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
      restart() ;
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
      getDynamicProblem().reset();
      //evaluations = 0 ;
      evaluations.reset();
    } else {
      //evaluations += getMaxPopulationSize() ;
      evaluations.increment(maxPopulationSize);
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
  public Observable<AlgorithmObservedData> getObservable() {
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
    Map<String, Object> algorithmData = new HashMap<>() ;
    algorithmData.put("numberOfIterations",completedIterations);
    algorithmData.put("algorithmName", getName()) ;
    algorithmData.put("problemName", problem.getName()) ;
    algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives()) ;
    algorithmData.put("referencePoint",newReferencePoint);
    List<S> emptyList = new ArrayList<>();
    observable.setChanged();
    observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>)emptyList, algorithmData));
  }
  public void setRestartStrategyForReferencePointChange(RestartStrategy<S> restartStrategyForReferencePointChange) {
    this.restartStrategyForReferencePointChange = restartStrategyForReferencePointChange ;
  }

  @Override
  public synchronized void update(
          Observable<ObservedValue<List<Double>>> observable,
          ObservedValue<List<Double>> data) {
    //if (data.getData().size() != getDynamicProblem().getNumberOfObjectives()) {
    ///  throw new JMetalException("The reference point size is not correct: " + data.getData().size()) ;
    //}
    List<S> newReferences = new ArrayList<>();

    int numberOfPoints = data.getValue().size()/getDynamicProblem().getNumberOfObjectives();
     int index = 0;
    for (int i = 0; i < numberOfPoints ; i++) {
      S solution = getDynamicProblem().createSolution();
      for (int j = 0; j < getDynamicProblem().getNumberOfObjectives(); j++) {
        solution.setObjective(j, data.getValue().get(index));
        index++;
      }
      newReferences.add(solution);

    }
    newReferencePoint = Optional.of(newReferences) ;

  }


}
