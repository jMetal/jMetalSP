package org.uma.jmetalsp.algorithm.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.qualityindicator.CoverageFront;

import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicNSGAIIBuilder<
        S extends Solution<?>,
        P extends DynamicProblem<S, ?>> {

    private int maxEvaluations;
    private int populationSize;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SolutionListEvaluator<S> evaluator;
    private Observable<AlgorithmObservedData> observable;
    private Comparator<S> dominanceComparator;
    private CoverageFront<PointSolution> coverageFront;
    private boolean autoUpdate;

    public DynamicNSGAIIBuilder(CrossoverOperator<S> crossoverOperator,
                                MutationOperator<S> mutationOperator,
                                Observable<AlgorithmObservedData> observable, CoverageFront<PointSolution> coverageFront) {
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.maxEvaluations = 25000;
        this.populationSize = 100;
        this.selectionOperator = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>());
        this.evaluator = new SequentialSolutionListEvaluator<S>();
        this.observable = observable;
        this.dominanceComparator = new DominanceComparator<>();
        this.coverageFront = coverageFront;
        this.autoUpdate = false;
    }

    public DynamicNSGAIIBuilder<S, P> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
        }
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        }

        this.populationSize = populationSize;

        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null) {
            throw new JMetalException("selectionOperator is null");
        }
        this.selectionOperator = selectionOperator;

        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("evaluator is null");
        }
        this.evaluator = evaluator;

        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;
        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setEvaluator(SolutionListEvaluator<S> evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setObservable(
            Observable<AlgorithmObservedData> observable) {
        this.observable = observable;
        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setDominanceComparator(Comparator<S> dominanceComparator) {
        this.dominanceComparator = dominanceComparator;
        return this;
    }

    public DynamicNSGAIIBuilder<S, P> setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        return this;
    }

    public DynamicNSGAII<S> build(P problem) {
        return new DynamicNSGAII(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator,
                selectionOperator, evaluator, dominanceComparator, observable, autoUpdate, coverageFront);
    }
}
