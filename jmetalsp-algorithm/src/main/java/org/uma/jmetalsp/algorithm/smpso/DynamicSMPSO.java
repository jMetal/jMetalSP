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

import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicSMPSO extends SMPSO
        implements DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData<DoubleSolution>> {

  private int completedIterations;
  private SolutionListEvaluator<DoubleSolution> evaluator;
  private DynamicProblem<DoubleSolution, ?> problem;
  private boolean stopAtTheEndOfTheCurrentIteration = false;
  private RestartStrategy<DoubleSolution> restartStrategyForProblemChange ;

  private Observable<AlgorithmObservedData<DoubleSolution>> observable;

  public DynamicSMPSO(DynamicProblem<DoubleSolution, ?> problem, int swarmSize, BoundedArchive<DoubleSolution> leaders,
                      MutationOperator<DoubleSolution> mutationOperator,
                      int maxIterations,
                      double r1Min, double r1Max, double r2Min, double r2Max,
                      double c1Min, double c1Max, double c2Min, double c2Max,
                      double weightMin, double weightMax,
                      double changeVelocity1, double changeVelocity2,
                      SolutionListEvaluator<DoubleSolution> evaluator,
                      Observable<AlgorithmObservedData<DoubleSolution>> observable) {
    super((DoubleProblem) problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max, r2Min, r2Max,
            c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);
    this.problem = problem;
    completedIterations = 0;
    this.evaluator = evaluator;
    this.observable = observable;
    this.restartStrategyForProblemChange = new RestartStrategy<>(
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

      algorithmData.put("numberOfIterations", completedIterations);
      observable.notifyObservers(new AlgorithmObservedData(getResult(), algorithmData));

      restart();
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration;
  }

  @Override
  public Observable<AlgorithmObservedData<DoubleSolution>> getObservable() {
    return this.observable;
  }

  @Override
  public void restart() {
    this.restartStrategyForProblemChange.restart(getSwarm(), getDynamicProblem());
    //SolutionListUtils.restart(getSwarm(), (DoubleProblem) getDynamicProblem(), 100);
    SolutionListUtils.removeSolutionsFromList(getResult(), getResult().size());
    evaluator.evaluate(getSwarm(), (DoubleProblem) getDynamicProblem());
    initializeVelocity(getSwarm());
    initializeParticlesMemory(getSwarm());
    initializeLeader(getSwarm());
    initProgress();
  }

  @Override
  public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
    this.restartStrategyForProblemChange = (RestartStrategy<DoubleSolution>) restartStrategy;
  }
}
