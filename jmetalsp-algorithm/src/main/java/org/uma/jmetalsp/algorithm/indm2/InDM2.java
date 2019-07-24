package org.uma.jmetalsp.algorithm.indm2;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.DynamicUpdate;
import org.uma.jmetalsp.InteractiveAlgorithm;
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
 * This class implements the InDM2 algorithm, which is described in:
 * InDM2: Interactive Dynamic Multi-Objective Decision Making using Evolutionary Algorithms. Antonio J. Nebro,
 * Ana B. Ruiz, Cristobal Barba-Gonzalez, Jose Garcia-Nieto, Jose F. Aldana, Mariano Luque. Submitted to Swarm and
 * Evolutionary Computation. June 2017.
 *
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InDM2<S extends Solution<?>>
        implements Algorithm<List<S>>, DynamicAlgorithm<List<S>,
        AlgorithmObservedData>, Observer<ObservedValue<List<Double>>> {
    protected Observable<AlgorithmObservedData> observable;
    Problem<S> problem;
    private int completedIterations;
    private boolean stopAtTheEndOfTheCurrentIteration = false;
    private Optional<List<Double>> newReferencePoint;
    // private Map<String,List> algorithmData;
    private RestartStrategy<S> restartStrategyForProblemChange;
    private RestartStrategy<S> restartStrategyForReferencePointChange;
    private String weightVectorsFileName = "";
    private InteractiveAlgorithm<S, List<S>> interactiveAlgorithm;
    private int evaluations;
    private int maxEvaluations;
    private List<S> lastReceivedFront;
    private boolean autoUpdate;
    private CoverageFront<PointSolution> coverageFront;

    public InDM2(Problem<S> problem, int populationSize, int maxEvaluations, InteractiveAlgorithm<S, List<S>> interactiveAlgorithm,
                 Observable<AlgorithmObservedData> observable, boolean autoUpdate, CoverageFront<PointSolution> coverageFront) {
        this.interactiveAlgorithm = interactiveAlgorithm;
        this.completedIterations = 0;
        this.observable = observable;
        this.evaluations = 0;
        this.maxEvaluations = maxEvaluations;
        this.newReferencePoint = Optional.ofNullable(null);
        this.autoUpdate = autoUpdate;
        this.coverageFront = coverageFront;
        //this.algorithmData = new HashMap<>();
        this.problem = problem;
        this.restartStrategyForProblemChange = new RestartStrategy<>(
                new RemoveFirstNSolutions<S>(populationSize),
                new CreateNRandomSolutions<S>());

        this.restartStrategyForReferencePointChange = new RestartStrategy<>(
                new RemoveFirstNSolutions<S>(populationSize),
                new CreateNRandomSolutions<S>());
    }


    @Override
    public DynamicProblem<S, ?> getDynamicProblem() {
        return (DynamicProblem<S, ?>) this.problem;
    }

    @Override
    public void restart() {
        interactiveAlgorithm.restart(restartStrategyForProblemChange);
        initProgress();
    }


    protected void initProgress() {
        evaluations = 0;
    }

    @Override
    public Observable<AlgorithmObservedData> getObservable() {
        return this.observable;
    }

    @Override
    public String getName() {
        return "InDM2-" + interactiveAlgorithm.getName();
    }

    @Override
    public String getDescription() {
        return "Interactive Dynamic Multi-Objective Decision Making algorithm";
    }


    protected boolean isStoppingConditionReached() {
        if (evaluations >= maxEvaluations) {

            boolean coverage = false;
            if (lastReceivedFront != null) {
                Front referenceFront = new ArrayFront(lastReceivedFront);
                coverageFront.updateFront(referenceFront);
                List<PointSolution> pointSolutionList = new ArrayList<>();
                List<S> list = interactiveAlgorithm.getResult();
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


                observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) interactiveAlgorithm.getResult(), algorithmData));
            }
            lastReceivedFront = interactiveAlgorithm.getResult();

            //this.restartStrategyForProblemChange.restart(interactiveAlgorithm.getPopulation(), (DynamicProblem<S, ?>)this.problem);
            restart();
            initProgress();
            completedIterations++;
        }
        return stopAtTheEndOfTheCurrentIteration;
    }


    protected void updateProgress() {
        if (newReferencePoint.isPresent()) {
            this.updateNewReferencePoint(newReferencePoint.get());
            this.restartStrategyForReferencePointChange.restart(interactiveAlgorithm.getPopulation(), (DynamicProblem<S, ?>) this.problem);
            restart();
            newReferencePoint = Optional.ofNullable(null);
            evaluations = 0;
        } else if (getDynamicProblem().hasTheProblemBeenModified()) {
            this.restartStrategyForProblemChange.restart(interactiveAlgorithm.getPopulation(), (DynamicProblem<S, ?>) this.problem);
            restart();
            getDynamicProblem().reset();
            evaluations = 0;
        } else {
            evaluations += interactiveAlgorithm.getPopulation().size();
        }
    }

    public void updateNewReferencePoint(List<Double> newReferencePoint) {

        interactiveAlgorithm.updatePointOfInterest(newReferencePoint);
        //this.updatePointOfInterest(referencePoint);
        Map<String, Object> algorithmData = new HashMap<>();
        algorithmData.put("numberOfIterations", completedIterations);
        algorithmData.put("algorithmName", getName());
        algorithmData.put("problemName", problem.getName());
        algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives());
        algorithmData.put("referencePoint", newReferencePoint);
        List<S> emptyList = new ArrayList<>();
        observable.setChanged();
        observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) emptyList, algorithmData));

    }

 /*@Override
  public synchronized void update(
          Observable<SingleObservedData<List<Double>>> observable,
          SingleObservedData<List<Double>> data) {
    if ((data.getData().size() % getDynamicProblem().getNumberOfObjectives())!=0) {
      throw new JMetalException("The reference point size is not correct: " + data.getData().size()) ;
    }




    newReferencePoint = Optional.of(data.getData()) ;
  }*/

    @Override
    public void setRestartStrategy(RestartStrategy<?> restartStrategyForProblemChange) {
        this.restartStrategyForProblemChange = (RestartStrategy<S>) restartStrategyForProblemChange;
    }

    public void setRestartStrategyForReferencePointChange(RestartStrategy<S> restartStrategyForReferencePointChange) {
        this.restartStrategyForReferencePointChange = restartStrategyForReferencePointChange;
    }

    @Override
    public void run() {
        List<S> population = interactiveAlgorithm.initializePopulation();
        interactiveAlgorithm.evaluate(population);
        initProgress();
        while (!isStoppingConditionReached()) {
            interactiveAlgorithm.compute();
            updateProgress();
        }
    }

    @Override
    public List<S> getResult() {
        return interactiveAlgorithm.getResult();
    }

    @Override
    public void update(Observable<ObservedValue<List<Double>>> observable, ObservedValue<List<Double>> data) {

        if ((data.getValue().size() % getDynamicProblem().getNumberOfObjectives()) != 0) {
            throw new JMetalException("The reference point size is not correct: " + data.getValue().size());
        }

        newReferencePoint = Optional.of(data.getValue());
    }
}
