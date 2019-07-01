package org.uma.jmetalsp.algorithm.nsgaiii;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.qualityindicator.CoverageFront;

import java.util.List;

public class DynamicNSGAIIIBuilder <
        S extends Solution<?>,
        P extends DynamicProblem<S, ?>> extends NSGAIIIBuilder<S> {
    private DynamicProblem<S, ?> problem ;
    private int maxIterations ;
    private int populationSize ;
    private CrossoverOperator<S> crossoverOperator ;
    private MutationOperator<S> mutationOperator ;
    private SelectionOperator<List<S>, S> selectionOperator ;
    private Observable<AlgorithmObservedData> observable;
    private SolutionListEvaluator<S> evaluator ;
    private CoverageFront<PointSolution> coverageFront;
    private boolean autoUpdate;
    public DynamicNSGAIIIBuilder(Problem<S> problem,Observable<AlgorithmObservedData> observable,CoverageFront<PointSolution> coverageFront) {
        super(problem);
        this.problem = (DynamicProblem)problem;
        this.maxIterations = 250;
        this.populationSize = 100;
        this.evaluator = new SequentialSolutionListEvaluator<S>() ;
        this.observable = observable;
        this.autoUpdate = false;
        this.coverageFront = coverageFront;
    }

    @Override
    public NSGAIIIBuilder<S> setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    @Override
    public NSGAIIIBuilder<S> setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    @Override
    public NSGAIIIBuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
        return this;
    }

    @Override
    public NSGAIIIBuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;
        return this;
    }

    @Override
    public NSGAIIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        this.selectionOperator = selectionOperator;
        return this;
    }

    @Override
    public NSGAIIIBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    public NSGAIIIBuilder<S> setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        return this;
    }

    @Override
    public SolutionListEvaluator<S> getEvaluator() {
        return this.evaluator;
    }

    @Override
    public Problem<S> getProblem() {
        return this.problem;
    }

    @Override
    public int getMaxIterations() {
        return this.maxIterations;
    }

    @Override
    public int getPopulationSize() {
        return this.populationSize;
    }

    @Override
    public CrossoverOperator<S> getCrossoverOperator() {
        return this.crossoverOperator;
    }

    @Override
    public MutationOperator<S> getMutationOperator() {
        return this.mutationOperator;
    }

    @Override
    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return this.selectionOperator;
    }

    @Override
    public NSGAIII<S> build() {
        return new DynamicNSGAIII<>(this,observable,autoUpdate,coverageFront);
    }

}
