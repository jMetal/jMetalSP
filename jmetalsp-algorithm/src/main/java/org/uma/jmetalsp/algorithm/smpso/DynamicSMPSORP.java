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

package org.uma.jmetalsp.algorithm.smpso;


import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
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
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicSMPSORP extends SMPSORP
        implements DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData>,
        Observer<ObservedValue<List<Double>>> {

    private int completedIterations;
    private SolutionListEvaluator<DoubleSolution> evaluator;
    private DynamicProblem<DoubleSolution, ?> problem;
    private boolean stopAtTheEndOfTheCurrentIteration = false;
    private RestartStrategy<DoubleSolution> restartStrategyForProblemChange;
    private RestartStrategy<DoubleSolution> restartStrategyForReferencePointChange;
    private Optional<List<DoubleSolution>> newReferencePoint;
    private Observable<AlgorithmObservedData> observable;
    private List<DoubleSolution> lastReceivedFront;
    private boolean autoUpdate;
    private CoverageFront<PointSolution> coverageFront;

    public DynamicSMPSORP(DynamicProblem<DoubleSolution, ?> problem, int swarmSize, List<ArchiveWithReferencePoint<DoubleSolution>> leaders, List<List<Double>> referencePoints, MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min, double r1Max, double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin, double weightMax, double changeVelocity1, double changeVelocity2, SolutionListEvaluator<DoubleSolution> evaluator,
                          Observable<AlgorithmObservedData> observable,boolean autoUpdate,CoverageFront<PointSolution> coverageFront) {
        super((DoubleProblem) problem, swarmSize, leaders, referencePoints, mutationOperator, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);
        this.problem = problem;
        completedIterations = 0;
        this.evaluator = evaluator;
        this.observable = observable;
        this.newReferencePoint = Optional.ofNullable(null);
        this.autoUpdate = autoUpdate;
        this.coverageFront = coverageFront;
        this.lastReceivedFront = null;
        this.restartStrategyForProblemChange = new RestartStrategy<>(
                new RemoveFirstNSolutions<>(swarmSize),
                new CreateNRandomSolutions<>());
        this.restartStrategyForReferencePointChange = new RestartStrategy<>(
                new RemoveFirstNSolutions<>(swarmSize),
                new CreateNRandomSolutions<>());
    }

    @Override
    public String getName() {
        return "DynamicSMPSO";
    }

    @Override
    public String getDescription() {
        return "Dynamic version of algorithm SMPSO";
    }

    @Override
    public DynamicProblem<DoubleSolution, ?> getDynamicProblem() {
        return problem;
    }


    @Override
    protected void updateProgress() {
        if (newReferencePoint.isPresent()) {
            this.updateNewReferencePoint(newReferencePoint.get());
            this.restartStrategyForReferencePointChange.restart(getSwarm(), getDynamicProblem());
            restart();
            evaluator.evaluate(getSwarm(), getDynamicProblem());
            newReferencePoint = Optional.ofNullable(null);
            getDynamicProblem().reset();
//      evaluations = 0 ;
        } else if (getDynamicProblem().hasTheProblemBeenModified()) {
            this.restartStrategyForProblemChange.restart(getSwarm(), getDynamicProblem());
            restart();
            evaluator.evaluate(getSwarm(), getDynamicProblem());
            getDynamicProblem().reset();
//      evaluations = 0 ;
        }
        int cont = iterations;
        iterations = cont + 1;
        updateLeadersDensityEstimator();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        if (iterations >= maxIterations) {
           /* double coverageValue = 1.0;
            if (lastReceivedFront != null) {
                Front referenceFront = new ArrayFront(lastReceivedFront);

                InvertedGenerationalDistance<PointSolution> igd =
                        new InvertedGenerationalDistance<PointSolution>(referenceFront);
                List<DoubleSolution> list = getResult();
                List<PointSolution> pointSolutionList = new ArrayList<>();
                for (DoubleSolution s : list) {
                    PointSolution pointSolution = new PointSolution(s);
                    pointSolutionList.add(pointSolution);
                }
                coverageValue = igd.evaluate(pointSolutionList);
            }


            if (coverageValue > 0.005) {
                observable.setChanged();
                Map<String, Object> algorithmData = new HashMap<>();

                algorithmData.put("numberOfIterations", completedIterations);
                algorithmData.put("algorithmName", getName());
                algorithmData.put("problemName", problem.getName());
                algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives());
                List<Solution<?>> aux = new ArrayList<>();
                List<DoubleSolution> solutions = getResult();
                for (DoubleSolution solution : solutions) {
                    aux.add(solution);
                }
                observable.notifyObservers(new AlgorithmObservedData(aux, algorithmData));
                //observable.notifyObservers(new AlgorithmObservedData(getResult(), algorithmData));
            }*/
            boolean coverage = false;
            if (lastReceivedFront != null) {
                Front referenceFront = new ArrayFront(lastReceivedFront);
                coverageFront.updateFront(referenceFront);
                List<PointSolution> pointSolutionList = new ArrayList<>();
                List<DoubleSolution> list = getResult();
                for (DoubleSolution s : list) {
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
                observable.notifyObservers(new AlgorithmObservedData((List)getResult(), algorithmData));
            }
            lastReceivedFront = getResult();

            restart();
            completedIterations++;
        }
        return stopAtTheEndOfTheCurrentIteration;
    }

    @Override
    public Observable<AlgorithmObservedData> getObservable() {
        return this.observable;
    }

    @Override
    public void restart() {
        this.restartStrategyForProblemChange.restart(getSwarm(), getDynamicProblem());
        //SolutionListUtils.restart(getSwarm(), (DoubleProblem) getDynamicProblem(), 100);
        SolutionListUtils.removeSolutionsFromList(getResult(), getResult().size());
        evaluator.evaluate(getSwarm(), getDynamicProblem());
        initializeVelocity(getSwarm());
        initializeParticlesMemory(getSwarm());
        cleanLeaders();
        initProgress();
    }

    private void cleanLeaders() {
        super.leaders = new ArrayList<>();

        for (int i = 0; i < referencePoints.size(); i++) {
            super.leaders.add(
                    new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(
                            swarmSize / referencePoints.size(), referencePoints.get(i)));
        }
        initializeLeader(getSwarm());
        List<DoubleSolution>referencePointSolutions = new ArrayList<>();
        for (int i = 0; i < referencePoints.size(); i++) {
            DoubleSolution refPoint = getDynamicProblem().createSolution();
            for (int j = 0; j < referencePoints.get(0).size(); j++) {
                refPoint.setObjective(j, referencePoints.get(i).get(j));
            }

            referencePointSolutions.add(refPoint);
        }
        this.setReferencePointSolutions(referencePointSolutions);
    }

    @Override
    public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
        this.restartStrategyForProblemChange = (RestartStrategy<DoubleSolution>) restartStrategy;
    }

    public void updateNewReferencePoint(List<DoubleSolution> newReferencePoints) {
        List<Double> referencePoint = new ArrayList<>();
        //Arrays.asList(
        //newReferencePoint.getObjective(0),
        //newReferencePoint.getObjective(1)) ;
        for (DoubleSolution point : newReferencePoints) {
            for (int i = 0; i < point.getNumberOfObjectives(); i++) {
                referencePoint.add(point.getObjective(i));
            }
        }
        referencePoints = new ArrayList<>();
        int numberOfPoints = referencePoint.size() / getDynamicProblem().getNumberOfObjectives();
        if (numberOfPoints == 1) {
            referencePoints.add(referencePoint);
        } else {
            int i = 0;
            while (i < referencePoint.size()) {
                int j = 0;
                List<Double> aux = new ArrayList<>();

                while (j < getDynamicProblem().getNumberOfObjectives()) {
                    aux.add(referencePoint.get(i));
                    j++;
                    i++;
                }
                referencePoints.add(aux);

            }
        }
        cleanLeaders();
        changeReferencePoints(referencePoints);

        Map<String, Object> algorithmData = new HashMap<>();
        algorithmData.put("numberOfIterations", completedIterations);
        algorithmData.put("algorithmName", getName());
        algorithmData.put("problemName", problem.getName());
        algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives());
        algorithmData.put("referencePoint", referencePoint);
        List<Solution<?>> emptyList = new ArrayList<>();
        observable.setChanged();
        observable.notifyObservers(new AlgorithmObservedData(emptyList, algorithmData));
    }

    public void setRestartStrategyForReferencePointChange(RestartStrategy<DoubleSolution> restartStrategyForReferencePointChange) {
        this.restartStrategyForReferencePointChange = restartStrategyForReferencePointChange;
    }

    @Override
    public void update(Observable<ObservedValue<List<Double>>> observable, ObservedValue<List<Double>> data) {
        List<DoubleSolution> newReferences = new ArrayList<>();

        int numberOfPoints = data.getValue().size() / getDynamicProblem().getNumberOfObjectives();
        int index = 0;
        for (int i = 0; i < numberOfPoints; i++) {
            DoubleSolution solution = getDynamicProblem().createSolution();
            for (int j = 0; j < getDynamicProblem().getNumberOfObjectives(); j++) {
                solution.setObjective(j, data.getValue().get(index));
                index++;
            }
            newReferences.add(solution);

        }
        newReferencePoint = Optional.of(newReferences);

    }
}
