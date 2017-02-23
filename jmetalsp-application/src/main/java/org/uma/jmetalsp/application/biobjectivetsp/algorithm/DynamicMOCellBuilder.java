package org.uma.jmetalsp.application.biobjectivetsp.algorithm;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;
import org.uma.jmetalsp.algorithm.AlgorithmBuilder;
import org.uma.jmetalsp.algorithm.DynamicMOCell;
import org.uma.jmetalsp.problem.tsp.DynamicMultiobjectiveTSP;

import java.util.List;

/**
 * Created by ajnebro on 22/4/16.
 */
public class DynamicMOCellBuilder
    implements AlgorithmBuilder<
        DynamicMOCell<PermutationSolution<Integer>>,
        DynamicMultiobjectiveTSP> {

  @Override
  public DynamicMOCell<PermutationSolution<Integer>> build(DynamicMultiobjectiveTSP problem) {
    DynamicMOCell<PermutationSolution<Integer>> algorithm;
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;
    double crossoverProbability = 0.9 ;
    crossover = new PMXCrossover(crossoverProbability) ;
    int populationSize=100;
    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    Neighborhood<PermutationSolution<Integer>> neighborhood=new C9((int)Math.sqrt(populationSize), (int)Math.sqrt(populationSize)) ;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;
    selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());
    algorithm = new DynamicMOCell(problem,50000,populationSize,new CrowdingDistanceArchive<DoubleSolution>(populationSize),
            neighborhood,crossover,mutation,selection, new SequentialSolutionListEvaluator<PermutationSolution<Integer>>() );

    return algorithm;
  }
}
