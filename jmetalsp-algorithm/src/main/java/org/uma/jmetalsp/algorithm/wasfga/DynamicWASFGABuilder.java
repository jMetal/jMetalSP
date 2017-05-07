package org.uma.jmetalsp.algorithm.wasfga;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class DynamicWASFGABuilder<
        S extends Solution<?>,
        P extends DynamicProblem<S, ?>,
        O extends Observable<AlgorithmObservedData>> {

    private CrossoverOperator<S> crossover;
    private MutationOperator<S> mutation;
    private SelectionOperator<List<S>, S> selection;
    private SolutionListEvaluator<S> evaluator;
    private List<Double> referencePoint = null;
    private double crossoverProbability;
    private double crossoverDistributionIndex;
    private double mutationProbability;
    private O observable;
    private double mutationDistributionIndex;
    private int maxIterations;
    private int populationSize;
    public DynamicWASFGABuilder(CrossoverOperator<S> crossoverOperator,
                                MutationOperator<S> mutationOperator,
                                O observable) {
        this.crossover = crossoverOperator;
        this.mutation = mutationOperator;
        this.selection = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>());
        this.evaluator = new SequentialSolutionListEvaluator<S>();
        this.crossoverProbability = 0.9;
        this.crossoverDistributionIndex = 20.0;
        this.mutationDistributionIndex = 20.0;
        this.maxIterations = 25000 ;
        this.populationSize = 100 ;
        this.observable = observable;
    }

    public DynamicWASFGABuilder<S,P,O> setCrossover(CrossoverOperator<S> crossover) {
        this.crossover = crossover;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setMutation(MutationOperator<S> mutation) {
        this.mutation = mutation;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setSelection(SelectionOperator<List<S>, S> selection) {
        this.selection = selection;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setEvaluator(SolutionListEvaluator<S> evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setCrossoverDistributionIndex(double crossoverDistributionIndex) {
        this.crossoverDistributionIndex = crossoverDistributionIndex;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setMutationDistributionIndex(double mutationDistributionIndex) {
        this.mutationDistributionIndex = mutationDistributionIndex;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public DynamicWASFGABuilder<S,P,O> setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public DynamicWASFGA<S, O> build(P problem) {
        referencePoint = new ArrayList<>();
        referencePoint.add(0.0);
        referencePoint.add(0.0);
        mutationProbability = 1.0 / problem.getNumberOfVariables();
        return new DynamicWASFGA(problem, populationSize, maxIterations, crossover, mutation, selection, evaluator, referencePoint, observable);

    }
}
