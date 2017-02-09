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
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.problem.DynamicProblem;

import java.util.List;


/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicSMPSO
        extends SMPSO
        implements DynamicAlgorithm<List<DoubleSolution>> {

  private int completedIterations ;
  protected SimpleMeasureManager measureManager ;
  protected BasicMeasure<List<DoubleSolution>> solutionListMeasure ;
  private SolutionListEvaluator<DoubleSolution> evaluator;
  private DynamicProblem<DoubleSolution,?> problem;
  private boolean stopAtTheEndOfTheCurrentIteration = false ;

  public DynamicSMPSO(DynamicProblem<DoubleSolution,?> problem, int swarmSize, BoundedArchive<DoubleSolution> leaders,
                      MutationOperator<DoubleSolution> mutationOperator,
                      int maxIterations,
                      double r1Min, double r1Max, double r2Min, double r2Max,
                      double c1Min, double c1Max, double c2Min, double c2Max,
                      double weightMin, double weightMax,
                      double changeVelocity1, double changeVelocity2,
                      SolutionListEvaluator<DoubleSolution> evaluator) {
    super((DoubleProblem) problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max, r2Min, r2Max,
				    c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);
    this.problem=problem;
    completedIterations = 0 ;
    this.evaluator=evaluator;
    solutionListMeasure = new BasicMeasure<>() ;
    measureManager = new SimpleMeasureManager() ;
    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
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
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }


  @Override
  public DynamicProblem<?, ?> getDynamicProblem() {
      return problem ;
  }

  @Override
  public int getCompletedIterations() {
    return completedIterations ;
  }


  @Override
    protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart(100);
      getDynamicProblem().reset();
    }
    int cont= getIterations();
    this.setIterations(cont+this.getSwarmSize());
    completedIterations ++ ;
    updateLeadersDensityEstimator();
  }

  @Override
  protected boolean isStoppingConditionReached() {
      if (getIterations() >= getMaxIterations()) {
      solutionListMeasure.push(getResult()) ;
      restart(100);
      completedIterations++;
    }
    return stopAtTheEndOfTheCurrentIteration;
  }

  @Override
  public void stopTheAlgorithm() {
    stopAtTheEndOfTheCurrentIteration = true ;
  }
  @Override
  public void restart(int percentageOfSolutionsToRemove){
    SolutionListUtils.restart(getSwarm(), (DoubleProblem)getDynamicProblem(), percentageOfSolutionsToRemove);
    //setSwarm(createInitialSwarm());
    SolutionListUtils.removeSolutionsFromList(getResult(),getResult().size());
    evaluator.evaluate(getSwarm(),(DoubleProblem) getDynamicProblem()) ;
    initializeVelocity(getSwarm());
    initializeParticlesMemory(getSwarm()) ;
    initializeLeader(getSwarm()) ;
    initProgress();
  }
}
