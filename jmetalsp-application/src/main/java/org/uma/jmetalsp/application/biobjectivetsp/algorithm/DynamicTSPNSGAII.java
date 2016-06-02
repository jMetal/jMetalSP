package org.uma.jmetalsp.application.biobjectivetsp.algorithm;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.algorithm.DynamicNSGAII;
import org.uma.jmetalsp.problem.DynamicProblem;

import java.util.List;

/**
 * Created by ajnebro on 22/4/16.
 */
public class DynamicTSPNSGAII extends DynamicNSGAII<PermutationSolution<Integer>> {

  public DynamicTSPNSGAII(DynamicProblem<PermutationSolution<Integer>, ?> problem, int maxEvaluations, int populationSize, CrossoverOperator<PermutationSolution<Integer>> crossoverOperator, MutationOperator<PermutationSolution<Integer>> mutationOperator, SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selectionOperator, SolutionListEvaluator<PermutationSolution<Integer>> evaluator) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
  }
}
