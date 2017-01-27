package org.uma.jmetalsp.application.fda.algorithm;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
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
import org.uma.jmetalsp.algorithm.DynamicNSGAII;
import org.uma.jmetalsp.problem.fda.FDA;
import org.uma.jmetalsp.problem.fda.fda2.FDA2;

import java.util.List;

/**
 * Created by ajnebro on 22/4/16.
 */
public class DynamicMOCellBuilder
    implements AlgorithmBuilder<
        DynamicMOCell<DoubleSolution>,
        FDA> {
    @Override
    public DynamicMOCell<DoubleSolution> build(FDA problem) {
      DynamicMOCell<DoubleSolution> algorithm;
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

      double crossoverProbability = 0.9 ;
      double crossoverDistributionIndex = 20.0 ;
      crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

      double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
      double mutationDistributionIndex = 20.0 ;
      mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

        int populationSize=100;
        selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());
      Neighborhood<DoubleSolution> neighborhood=new C9((int)Math.sqrt(populationSize), (int)Math.sqrt(populationSize)) ;
      algorithm = new DynamicMOCell(problem,750000,100,new CrowdingDistanceArchive<DoubleSolution>(populationSize),
              neighborhood,crossover,mutation,selection, new SequentialSolutionListEvaluator<DoubleSolution>() );


        return algorithm;
    }



}
