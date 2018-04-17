package org.uma.jmetalsp.algorithm.wasfga;

import java.util.ArrayList;
import java.util.Optional;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;

/**
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class DynamicWASFGA<S extends Solution<?>>
        extends WASFGA<S>
    implements DynamicAlgorithm<List<S>, AlgorithmObservedData<S>>,
    Observer<SingleObservedData<List<Double>>> {
  private int completedIterations;
  private boolean stopAtTheEndOfTheCurrentIteration = false;
  private RestartStrategy<S> restartStrategyForProblemChange ;
  private String weightVectorsFileName;
  Observable<AlgorithmObservedData<S>> observable ;
  private Optional<List<S>> newReferencePoint ;
  private Map<String, List> algorithmData;
  private double epsilon;
  private RestartStrategy<S> restartStrategyForReferencePointChange ;
  public DynamicWASFGA(Problem<S> problem,
                       int populationSize,
                       int maxIterations,
                       CrossoverOperator<S> crossoverOperator,
                       MutationOperator<S> mutationOperator,
                       SelectionOperator<List<S>, S> selectionOperator,
                       SolutionListEvaluator<S> evaluator,
                       List<Double> referencePoint,
                        double epsilon,
                       Observable<AlgorithmObservedData<S>> observable) {
    super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator,epsilon, referencePoint);
    this.completedIterations = 0;
    this.observable = observable;
    this.evaluations = 0;
    this.maxEvaluations = maxIterations;
    this.weightVectorsFileName= null;
    this.epsilon = epsilon;
    this.newReferencePoint = Optional.ofNullable(null);
    this.restartStrategyForProblemChange = new RestartStrategy<>(
        new RemoveFirstNSolutions<S>(populationSize),
        new CreateNRandomSolutions<S>()) ;
    this.restartStrategyForReferencePointChange = new RestartStrategy<>(
        new RemoveFirstNSolutions<S>(populationSize),
        new CreateNRandomSolutions<S>()) ;
  }
  public DynamicWASFGA(Problem<S> problem,
      int populationSize,
      int maxIterations,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator,
      List<Double> referencePoint,
      double epsilon,
      Observable<AlgorithmObservedData<S>> observable,String weightVectorsFileName) {
    super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator,epsilon, referencePoint,weightVectorsFileName);
    this.completedIterations = 0;
    this.observable = observable;
    this.evaluations = 0;
    this.maxEvaluations = maxIterations;
    this.weightVectorsFileName= weightVectorsFileName;
    this.epsilon = epsilon;
    this.newReferencePoint = Optional.ofNullable(null);
    this.restartStrategyForProblemChange = new RestartStrategy<>(
        new RemoveFirstNSolutions<S>(populationSize),
        new CreateNRandomSolutions<S>()) ;
    this.restartStrategyForReferencePointChange = new RestartStrategy<>(
        new RemoveFirstNSolutions<S>(populationSize),
        new CreateNRandomSolutions<S>()) ;
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

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    for (S solution : population) {
      getProblem().evaluate(solution);
      //((ConstrainedProblem) getDynamicProblem()).evaluateConstraints(solution);
    }

    return population;
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
    /*if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart();
      getDynamicProblem().reset();
    }
    evaluations++;*/

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
      getDynamicProblem().reset();
      evaluations = 0 ;
    } else {
      evaluations ++ ;
    }

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

  @Override
  public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
    this.restartStrategyForProblemChange = (RestartStrategy<S>) restartStrategy;
  }

  @Override
  public void update(Observable<SingleObservedData<List<Double>>> observable,
      SingleObservedData<List<Double>> data) {

  }

  public RestartStrategy<S> getRestartStrategyForProblemChange() {
    return restartStrategyForProblemChange;
  }

  public void setRestartStrategyForProblemChange(
      RestartStrategy<S> restartStrategyForProblemChange) {
    this.restartStrategyForProblemChange = restartStrategyForProblemChange;
  }

  public String getWeightVectorsFileName() {
    return weightVectorsFileName;
  }

  public void setWeightVectorsFileName(String weightVectorsFileName) {
    this.weightVectorsFileName = weightVectorsFileName;
  }
}
