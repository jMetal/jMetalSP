package org.uma.jmetalsp.algorithm.wasfga;

import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGAConstraint;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class DynamicWASFGAConstraints<S extends Solution<?>>
        extends WASFGAConstraint<S>
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData<S>> {
  private int completedIterations;
  private boolean stopAtTheEndOfTheCurrentIteration = false;
  private RestartStrategy<S> restartStrategyForProblemChange ;

  Observable<AlgorithmObservedData<S>> observable ;

  private Map<String, List> algorithmData;

  public DynamicWASFGAConstraints(Problem<S> problem,
                                  int populationSize,
                                  int maxIterations,
                                  CrossoverOperator<S> crossoverOperator,
                                  MutationOperator<S> mutationOperator,
                                  SelectionOperator<List<S>, S> selectionOperator,
                                  SolutionListEvaluator<S> evaluator,
                                  List<Double> referencePoint,
                                  Observable<AlgorithmObservedData<S>> observable) {
    super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator, referencePoint);
    completedIterations = 0;
    this.observable = observable;
    evaluations = 0;
    maxEvaluations = maxIterations;
  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return (DynamicProblem<S, ?>) super.getProblem();
  }

  @Override
  public void restart() {
    this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>)getProblem());
    this.evaluatePopulation(this.getPopulation());
    this.initProgress();
    this.specificMOEAComputations();
  }

 /* @Override
  protected List<S> evaluatePopulation(List<S> population) {
    for (S solution : population) {
      getProblem().evaluate(solution);
      ((ConstrainedProblem) getDynamicProblem()).evaluateConstraints(solution);
    }

    return population;
  }*/


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
    return "Dynamic WASF-GA";
  }

  @Override
  public String getDescription() {
    return "Dynamic version of algorithm WASFGA";
  }

  @Override
  protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {
      observable.setChanged();
      Map<String, Object> algorithmData = new HashMap<>() ;

      algorithmData.put("numberOfIterations",completedIterations);
      observable.notifyObservers(new AlgorithmObservedData<S>(getPopulation(), algorithmData));

      restart();
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration;
  }

  @Override
  protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart();
      getDynamicProblem().reset();
    }
    evaluations++;
  }

  @Override
  public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
    this.restartStrategyForProblemChange = (RestartStrategy<S>) restartStrategy;
  }
}
