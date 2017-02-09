package org.uma.jmetalsp.algorithm.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetalsp.algorithm.DynamicAlgorithmBuilder;
import org.uma.jmetalsp.problem.DynamicProblem;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicNSGAIIBuilder<
				S extends Solution<?>,
				P extends DynamicProblem<S, ?>> implements DynamicAlgorithmBuilder<DynamicNSGAII<S>, P> {

	private int maxEvaluations;
	private int populationSize;
	private CrossoverOperator<S> crossoverOperator;
	private MutationOperator<S> mutationOperator;
	private SelectionOperator<List<S>, S> selectionOperator;
	private SolutionListEvaluator<S> evaluator;

	public DynamicNSGAIIBuilder(CrossoverOperator<S> crossoverOperator,
	                            MutationOperator<S> mutationOperator) {
		this.crossoverOperator = crossoverOperator ;
		this.mutationOperator = mutationOperator;
		this.maxEvaluations = 25000 ;
		this.populationSize = 100 ;
		this.selectionOperator = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>()) ;
		this.evaluator = new SequentialSolutionListEvaluator<S>();
	}

	public DynamicNSGAIIBuilder<S,P> setMaxEvaluations(int maxEvaluations) {
		if (maxEvaluations < 0) {
			throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
		}
		this.maxEvaluations = maxEvaluations;

		return this;
	}

	public DynamicNSGAIIBuilder<S,P> setPopulationSize(int populationSize) {
		if (populationSize < 0) {
			throw new JMetalException("Population size is negative: " + populationSize);
		}

		this.populationSize = populationSize;

		return this;
	}

	public DynamicNSGAIIBuilder<S,P> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
		if (selectionOperator == null) {
			throw new JMetalException("selectionOperator is null");
		}
		this.selectionOperator = selectionOperator;

		return this;
	}

	public DynamicNSGAIIBuilder<S,P> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
		if (evaluator == null) {
			throw new JMetalException("evaluator is null");
		}
		this.evaluator = evaluator;

		return this;
	}

	@Override
	public DynamicNSGAII<S> build(P problem) {
		return new DynamicNSGAII<S>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator,
						selectionOperator, evaluator);
	}
}
