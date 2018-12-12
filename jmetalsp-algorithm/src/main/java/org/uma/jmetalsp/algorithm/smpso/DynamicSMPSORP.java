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
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicSMPSORP extends SMPSORP
        implements DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData> {

  private int completedIterations;
  private SolutionListEvaluator<DoubleSolution> evaluator;
  private DynamicProblem<DoubleSolution, ?> problem;
  private boolean stopAtTheEndOfTheCurrentIteration = false;
  private RestartStrategy<DoubleSolution> restartStrategyForProblemChange ;
  private RestartStrategy<DoubleSolution> restartStrategyForReferencePointChange ;

  private Observable<AlgorithmObservedData> observable;

  public DynamicSMPSORP(DynamicProblem<DoubleSolution, ?> problem, int swarmSize, List<ArchiveWithReferencePoint<DoubleSolution>> leaders, List<List<Double>> referencePoints, MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min, double r1Max, double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin, double weightMax, double changeVelocity1, double changeVelocity2, SolutionListEvaluator<DoubleSolution> evaluator,
                        Observable<AlgorithmObservedData> observable) {
    super((DoubleProblem) problem, swarmSize, leaders, referencePoints, mutationOperator, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);
    this.problem = problem;
    completedIterations = 0;
    this.evaluator = evaluator;
    this.observable = observable;
    this.restartStrategyForProblemChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<>(swarmSize),
            new CreateNRandomSolutions<>()) ;
    this.restartStrategyForReferencePointChange = new RestartStrategy<>(
            new RemoveFirstNSolutions<>(swarmSize),
            new CreateNRandomSolutions<>()) ;
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
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart();
      getDynamicProblem().reset();
    }
    int cont = getIterations();
    this.setIterations(cont + 1);
    updateLeadersDensityEstimator();
  }

  @Override
  protected boolean isStoppingConditionReached() {
    if (getIterations() >= getMaxIterations()) {
      observable.setChanged();
      Map<String, Object> algorithmData = new HashMap<>() ;

      algorithmData.put("numberOfIterations",completedIterations);
      algorithmData.put("algorithmName", getName()) ;
      algorithmData.put("problemName", problem.getName()) ;
      algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives()) ;
      List<Solution<?>> aux = new ArrayList<>();
      List<DoubleSolution> solutions = getResult();
      for (DoubleSolution solution:solutions) {
        aux.add(solution);
      }
      observable.notifyObservers(new AlgorithmObservedData(aux, algorithmData));
      //observable.notifyObservers(new AlgorithmObservedData(getResult(), algorithmData));

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
    initializeLeader(getSwarm());
    initProgress();
  }
  private void cleanLeaders(){
    super.leaders = new ArrayList<>();

    for (int i = 0; i < referencePoints.size(); i++) {
      super.leaders.add(
              new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(
                      swarmSize/referencePoints.size(), referencePoints.get(i))) ;
    }
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
    for (DoubleSolution point:newReferencePoints) {
      for (int i = 0; i < point.getNumberOfObjectives(); i++) {
        referencePoint.add(point.getObjective(i));
      }
    }
    List<List<Double>> referencePoints = new ArrayList<>();
    int numberOfPoints= newReferencePoints.size()/getDynamicProblem().getNumberOfObjectives();
    int i=0;
    while (i<newReferencePoints.size()){
      int j= numberOfPoints-1;
      List<Double> aux = new ArrayList<>();
      while(j>=0){
        aux.add(referencePoint.get(i));
        i++;
        j--;
      }
      referencePoints.add(aux);
    }
    changeReferencePoints(referencePoints);


    Map<String, Object> algorithmData = new HashMap<>() ;
    algorithmData.put("numberOfIterations",completedIterations);
    algorithmData.put("algorithmName", getName()) ;
    algorithmData.put("problemName", problem.getName()) ;
    algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives()) ;
    algorithmData.put("referencePoint",referencePoints);
    List<Solution<?>> emptyList = new ArrayList<>();
    observable.setChanged();
    observable.notifyObservers(new AlgorithmObservedData(emptyList, algorithmData));
  }
  public void setRestartStrategyForReferencePointChange(RestartStrategy<DoubleSolution> restartStrategyForReferencePointChange) {
    this.restartStrategyForReferencePointChange = restartStrategyForReferencePointChange ;
  }
}
