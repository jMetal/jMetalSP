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
import org.uma.jmetalsp.observeddata.AlgorithmObservedData2;
import org.uma.jmetalsp.observeddata.ListObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.util.*;

/**
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class InDM2<S extends Solution<?>>
        extends WASFGA<S>
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData2, Observable<AlgorithmObservedData2>>,
        Observer<ListObservedData<Double>> {
  private int completedIterations;
  private boolean stopAtTheEndOfTheCurrentIteration = false;
  private Optional<S> newReferencePoint ;
  private Map<String,List> algorithmData;

  Observable<AlgorithmObservedData2> observable;

  public InDM2(Problem<S> problem, int populationSize, int maxIterations, CrossoverOperator<S> crossoverOperator,
               MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator,
               SolutionListEvaluator<S> evaluator, List<Double> referencePoint,
               Observable<AlgorithmObservedData2> observable) {
    super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator,
            referencePoint);
    completedIterations = 0;
    this.observable = observable;
    evaluations = 0;
    maxEvaluations = maxIterations;
    newReferencePoint = Optional.ofNullable(null);
    this.algorithmData = new HashMap<>();
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
  public Observable<AlgorithmObservedData2> getObservable() {
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
      //System.out.println("End. Ref Point: " + this.getReferencePoint().get(0) + ", " + this.getReferencePoint().get(1)) ;
      observable.setChanged();
      List<Integer> data= new ArrayList<>();
      data.add(completedIterations);
      algorithmData.put("numberOfIterations",data);

      observable.notifyObservers(new AlgorithmObservedData2(getPopulation(), algorithmData));
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

    if (newReferencePoint.isPresent()) {
      this.updateNewReferencePoint(newReferencePoint.get());
      restart(100) ;
      newReferencePoint = Optional.ofNullable(null);
    }
    evaluations++;
  }

  public void updateNewReferencePoint(S newReferencePoint) {
    List<Double> referencePoint = Arrays.asList(
            newReferencePoint.getObjective(0),
            newReferencePoint.getObjective(1)) ;
    this.updatePointOfInterest(referencePoint);
    List<Integer> data= new ArrayList<>();
    data.add(completedIterations);
    algorithmData.put("numberOfIterations",data);
    algorithmData.put("referencePoints",referencePoint);
    observable.notifyObservers(new AlgorithmObservedData2(getPopulation(), algorithmData));
  }

  @Override
  public synchronized void update(Observable<ListObservedData<Double>> observable, ListObservedData<Double> data) {
    if (data.getList().size() != getDynamicProblem().getNumberOfObjectives()) {
      throw new JMetalException("The reference point size is not correct: " + data.getList().size()) ;
    }

    S solution = getDynamicProblem().createSolution();

    for (int i = 0; i < getDynamicProblem().getNumberOfObjectives(); i++) {
      solution.setObjective(i, data.getList().get(i));
    }

    newReferencePoint = Optional.of(solution) ;
  }
}
