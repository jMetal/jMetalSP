package org.uma.jmetalsp.algorithm.indm2;

import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
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
 * This class implements the InDM2 algorithm, which is described in:
 * InDM2: Interactive Dynamic Multi-Objective Decision Making using Evolutionary Algorithms. Antonio J. Nebro,
 * Ana B. Ruiz, Cristobal Barba-Gonzalez, Jose Garcia-Nieto, Jose F. Aldana, Mariano Luque. Submitted to Swarm and
 * Evolutionary Computation. June 2017.
 *
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class InDM2<S extends Solution<?>>
        extends WASFGA<S>
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData<S>>,
        Observer<SingleObservedData<List<Double>>> {
  private int completedIterations;
  private boolean stopAtTheEndOfTheCurrentIteration = false;
  private Optional<S> newReferencePoint ;
  private Map<String,List> algorithmData;
  private RestartStrategy<S> restartStrategyForProblemChange ;
  private RestartStrategy<S> restartStrategyForReferencePointChange ;

  Observable<AlgorithmObservedData<S>> observable;

  public InDM2(Problem<S> problem, int populationSize, int maxEvaluations, CrossoverOperator<S> crossoverOperator,
               MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator,
               SolutionListEvaluator<S> evaluator, List<Double> referencePoint,
               Observable<AlgorithmObservedData<S>> observable) {
    super(problem, populationSize, maxEvaluations, crossoverOperator, mutationOperator, selectionOperator, evaluator,
            referencePoint);
    completedIterations = 0;
    this.observable = observable;
    this.evaluations = 0;
    this.maxEvaluations = maxEvaluations ;
    newReferencePoint = Optional.ofNullable(null);
    this.algorithmData = new HashMap<>();
    this.restartStrategyForProblemChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<S>(populationSize),
            new CreateNRandomSolutions<S>(populationSize)) ;

    this.restartStrategyForReferencePointChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<S>(populationSize),
            new CreateNRandomSolutions<S>(populationSize)) ;
  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return (DynamicProblem<S, ?>) super.getProblem();
  }

  @Override
  public void restart() {
    this.evaluatePopulation(this.getPopulation());
    this.initProgress();
    this.specificMOEAComputations();
  }

  @Override
  protected void initProgress() {
    evaluations = 0;
  }

  @Override
  public Observable<AlgorithmObservedData<S>> getObservable() {
    return this.observable;
  }

  @Override
  public String getName() {
    return "InDM2";
  }

  @Override
  public String getDescription() {
    return "Interactive Dynamic Multi-Objective Decision Making algorithm";
  }

  @Override
  protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {
      observable.setChanged();
      Map<String, Object> algorithmData = new HashMap<>() ;

      algorithmData.put("numberOfIterations",completedIterations);
      observable.notifyObservers(new AlgorithmObservedData<S>(getPopulation(), algorithmData));

      this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
      restart();
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration;
  }

  @Override
  protected void updateProgress() {
    if (newReferencePoint.isPresent()) {
      this.updateNewReferencePoint(newReferencePoint.get());
      this.restartStrategyForReferencePointChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());

      restart() ;
      newReferencePoint = Optional.ofNullable(null);
      evaluations = 0 ;
    } else if (getDynamicProblem().hasTheProblemBeenModified()) {
      this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
      restart();
      getDynamicProblem().reset();
      evaluations = 0 ;
    } else {
      evaluations+=this.getPopulationSize();
    }
  }

  public void updateNewReferencePoint(S newReferencePoint) {
    List<Double> referencePoint = Arrays.asList(
            newReferencePoint.getObjective(0),
            newReferencePoint.getObjective(1)) ;
    this.updatePointOfInterest(referencePoint);
    algorithmData.put("newReferencePoint",referencePoint);
    List<S> emptyList = new ArrayList<>();
    observable.setChanged();
    observable.notifyObservers(new AlgorithmObservedData(emptyList, algorithmData));
  }

  @Override
  public synchronized void update(
          Observable<SingleObservedData<List<Double>>> observable,
          SingleObservedData<List<Double>> data) {
    if (data.getData().size() != getDynamicProblem().getNumberOfObjectives()) {
      throw new JMetalException("The reference point size is not correct: " + data.getData().size()) ;
    }

    S solution = getDynamicProblem().createSolution();

    for (int i = 0; i < getDynamicProblem().getNumberOfObjectives(); i++) {
      solution.setObjective(i, data.getData().get(i));
    }

    newReferencePoint = Optional.of(solution) ;
  }

  public void setRestartStrategyForProblemChange(RestartStrategy<S> restartStrategyForProblemChange) {
    this.restartStrategyForProblemChange = restartStrategyForProblemChange ;
  }

  public void setRestartStrategyForReferencePointChange(RestartStrategy<S> restartStrategyForReferencePointChange) {
    this.restartStrategyForReferencePointChange = restartStrategyForReferencePointChange ;
  }
}
