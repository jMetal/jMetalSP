package org.uma.jmetalsp.algorithm.rnsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.qualityindicator.CoverageFront;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicRNSGAIIBuilder<
        S extends Solution<?>,
        P extends DynamicProblem<S, ?>> {

    private int maxEvaluations;
    private int populationSize;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SolutionListEvaluator<S> evaluator;
    private Observable<AlgorithmObservedData> observable;
    private List<Double> referencePoint;
    private double epsilon;
    private int matingPoolSize;
    private int offspringPopulationSize;
    private boolean autoUpdate;
    private CoverageFront<PointSolution> coverageFront;

    public DynamicRNSGAIIBuilder(CrossoverOperator<S> crossoverOperator,
                                 MutationOperator<S> mutationOperator,
                                 Observable<AlgorithmObservedData> observable, List<Double> referencePoint,
                                 double epsilon,
                                 CoverageFront<PointSolution> coverageFront) {
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.maxEvaluations = 25000;
        this.matingPoolSize = 100;
        this.offspringPopulationSize = 100;
        this.populationSize = 100;
        this.selectionOperator = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>());
        this.evaluator = new SequentialSolutionListEvaluator<S>();
        this.observable = observable;
        this.referencePoint = referencePoint;
        this.epsilon = epsilon;
        this.autoUpdate = false;
        this.coverageFront = coverageFront;
    }

    public DynamicRNSGAIIBuilder<S, P> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
        }
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        if (crossoverOperator == null) {
            throw new JMetalException("Crossover Operator is null");
        }
        this.crossoverOperator = crossoverOperator;
        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setMutationOperator(MutationOperator<S> mutationOperator) {
        if (mutationOperator == null) {
            throw new JMetalException("Mutation Operator is null");
        }
        this.mutationOperator = mutationOperator;
        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        }

        this.populationSize = populationSize;

        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null) {
            throw new JMetalException("selectionOperator is null");
        }
        this.selectionOperator = selectionOperator;

        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("evaluator is null");
        }
        this.evaluator = evaluator;

        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setReferencePoint(List<Double> referencePoint) {
        if (referencePoint == null) {
            throw new JMetalException("referencePoint is null");
        }
        this.referencePoint = referencePoint;
        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setEpsilon(double epsilon) {
        if (epsilon < 0) {
            throw new JMetalException("epsilon is negative: " + epsilon);
        }
        this.epsilon = epsilon;
        return this;
    }

    public DynamicRNSGAIIBuilder<S, P> setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        return this;
    }

    public DynamicRNSGAII<S> build(P problem) {
        return new DynamicRNSGAII(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator,
                selectionOperator, evaluator, observable, referencePoint, epsilon, autoUpdate, coverageFront);
    }
}
