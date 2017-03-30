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
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.perception.Observable;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicNSGAIIBuilder<
				S extends Solution<?>,
				P extends DynamicProblem<S, ?>,
				O extends Observable<AlgorithmObservedData>>  {

	private int maxEvaluations;
	private int populationSize;
	private CrossoverOperator<S> crossoverOperator;
	private MutationOperator<S> mutationOperator;
	private SelectionOperator<List<S>, S> selectionOperator;
	private SolutionListEvaluator<S> evaluator;
  private O observable ;

	public DynamicNSGAIIBuilder(CrossoverOperator<S> crossoverOperator,
	                            MutationOperator<S> mutationOperator,
															O observable) {
		this.crossoverOperator = crossoverOperator ;
		this.mutationOperator = mutationOperator;
		this.maxEvaluations = 25000 ;
		this.populationSize = 100 ;
		this.selectionOperator = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>()) ;
		this.evaluator = new SequentialSolutionListEvaluator<S>();
		this.observable = observable ;
	}

	public DynamicNSGAIIBuilder<S,P,O> setMaxEvaluations(int maxEvaluations) {
		if (maxEvaluations < 0) {
			throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
		}
		this.maxEvaluations = maxEvaluations;

		return this;
	}

	public DynamicNSGAIIBuilder<S,P,O> setPopulationSize(int populationSize) {
		if (populationSize < 0) {
			throw new JMetalException("Population size is negative: " + populationSize);
		}

		this.populationSize = populationSize;

		return this;
	}

	public DynamicNSGAIIBuilder<S,P,O> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
		if (selectionOperator == null) {
			throw new JMetalException("selectionOperator is null");
		}
		this.selectionOperator = selectionOperator;

		return this;
	}

	public DynamicNSGAIIBuilder<S,P,O> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
		if (evaluator == null) {
			throw new JMetalException("evaluator is null");
		}
		this.evaluator = evaluator;

		return this;
	}

	public DynamicNSGAII<S, O> build(P problem) {
		return new DynamicNSGAII<S, O>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator,
						selectionOperator, evaluator, observable);
	}
}
