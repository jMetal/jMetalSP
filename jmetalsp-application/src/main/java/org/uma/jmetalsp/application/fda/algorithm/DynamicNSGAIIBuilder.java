package org.uma.jmetalsp.application.fda.algorithm;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetalsp.algorithm.AlgorithmBuilder;
import org.uma.jmetalsp.algorithm.DynamicNSGAII;
import org.uma.jmetalsp.application.fda.problem.fda1.FDA1;
import org.uma.jmetalsp.application.fda.problem.fda2.FDA2;

import java.util.List;

/**
 * Created by ajnebro on 22/4/16.
 */
public class DynamicNSGAIIBuilder
    implements AlgorithmBuilder<
        DynamicNSGAII<DoubleSolution>,
        FDA2> {
    @Override
    public DynamicNSGAII<DoubleSolution> build(FDA2 problem) {
        DynamicNSGAII<DoubleSolution> algorithm;
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

        selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

        algorithm = new DynamicNSGAII(problem, 250000, 100, crossover, mutation, selection,
                new SequentialSolutionListEvaluator<DoubleSolution>()) ;

        return algorithm;
    }



}
