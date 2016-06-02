package org.uma.jmetalsp.algorithm;

import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.problem.DynamicProblem;

import java.util.List;

/**
 * Created by ajnebro on 18/4/16.
 */
public class DynamicSMPSO
    extends SMPSO
    implements DynamicAlgorithm<List<DoubleSolution>> {

  protected SimpleMeasureManager measureManager ;

  public DynamicSMPSO(DoubleProblem problem, int swarmSize, BoundedArchive<DoubleSolution> leaders, MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min, double r1Max, double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin, double weightMax, double changeVelocity1, double changeVelocity2, SolutionListEvaluator<DoubleSolution> evaluator) {
    super(problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);
  }

  @Override
  public String getName() {
    return "DynamicNSGAII";
  }

  @Override
  public String getDescription() {
    return "Dynamic version of algorithm NSGA-II";
  }

  @Override
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }

  @Override
  public DynamicProblem getDynamicProblem() {
    return null;
  }

  @Override
  public int getCompletedIterations() {
    return 0;
  }
}
