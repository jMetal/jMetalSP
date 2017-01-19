package org.uma.jmetalsp.application.biobjectivetsp.algorithm;

import org.uma.jmetalsp.algorithm.AlgorithmBuilder;
import org.uma.jmetalsp.algorithm.DynamicNSGAII;
import org.uma.jmetalsp.application.biobjectivetsp.problem.DynamicMultiobjectiveTSP;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;


import java.util.List;

/**
 * Created by ajnebro on 22/4/16.
 */
public class DynamicNSGAIIBuilder
    implements AlgorithmBuilder<
        DynamicNSGAII<PermutationSolution<Integer>>,
        DynamicMultiobjectiveTSP> {

  @Override
  public DynamicNSGAII<PermutationSolution<Integer>> build(DynamicMultiobjectiveTSP problem) {
    DynamicNSGAII<PermutationSolution<Integer>> algorithm;
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

    crossover = new PMXCrossover(0.9) ;

    double mutationProbability = 0.2 ;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

    selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    algorithm = new DynamicNSGAII(problem, 100000, 100, crossover, mutation, selection,
        new SequentialSolutionListEvaluator<PermutationSolution<Integer>>()) ;

    return algorithm;
  }
}
