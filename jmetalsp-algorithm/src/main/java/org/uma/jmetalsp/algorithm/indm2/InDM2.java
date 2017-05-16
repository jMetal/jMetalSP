package org.uma.jmetalsp.algorithm.indm2;

import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ListObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.util.List;

/**
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class InDM2<S extends Solution<?>>
        extends WASFGA<S>
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData, Observable<AlgorithmObservedData>>, Observer<ListObservedData<Double>> {
  private int completedIterations;
  private boolean stopAtTheEndOfTheCurrentIteration = false;

  Observable<AlgorithmObservedData> observable;

  public InDM2(Problem<S> problem, int populationSize, int maxIterations, CrossoverOperator<S> crossoverOperator,
               MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator,
               SolutionListEvaluator<S> evaluator, List<Double> referencePoint,
               Observable<AlgorithmObservedData> observable) {
    super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator,
            referencePoint);
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
  public int getCompletedIterations() {
    return completedIterations;
  }

  @Override
  public void stopTheAlgorithm() {
    stopAtTheEndOfTheCurrentIteration = true;
  }

  @Override
  public void restart(int percentageOfSolutionsToRemove) {
    SolutionListUtils.restart(getPopulation(), getDynamicProblem(), percentageOfSolutionsToRemove);
    this.evaluatePopulation(this.getPopulation());
    this.initProgress();
    this.specificMOEAComputations();
  }

  @Override
  protected void initProgress() {
    evaluations = 0;
  }

  @Override
  public Observable<AlgorithmObservedData> getObservable() {
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
      observable.notifyObservers(new AlgorithmObservedData(getPopulation(), completedIterations));
      restart(100);
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration;
  }

  @Override
  protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart(100);
      getDynamicProblem().reset();
    }
    evaluations++;
  }

  @Override
  public synchronized void update(Observable<ListObservedData<Double>> observable, ListObservedData<Double> data) {
    if (data.getList().size() != getDynamicProblem().getNumberOfObjectives()) {
      throw new JMetalException("The reference point size is not correct: " + data.getList().size()) ;
    }
    ////// OJO A LA SINCRONIZACION ....
    
    S solution = getDynamicProblem().createSolution();

    for (int i = 0; i < getDynamicProblem().getNumberOfObjectives(); i++) {
      solution.setObjective(i, data.getList().get(i));
    }

    this.updateReferencePoint(solution);
  }
}
