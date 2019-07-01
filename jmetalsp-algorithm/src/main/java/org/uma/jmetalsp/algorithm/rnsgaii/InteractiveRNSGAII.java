package org.uma.jmetalsp.algorithm.rnsgaii;

import org.uma.jmetal.algorithm.multiobjective.rnsgaii.RNSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.InteractiveAlgorithm;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;

import java.util.List;

public class InteractiveRNSGAII<S extends Solution<?>> extends RNSGAII<S> implements InteractiveAlgorithm<S, List<S>> {

    private List<S> offspringPopulation;
    private List<S> matingPopulation;

    /**
     * Constructor
     */

    public InteractiveRNSGAII(Problem problem, int populationSize, int matingPoolSize, int offspringPopulationSize,
                              CrossoverOperator crossoverOperator,
                              MutationOperator mutationOperator,
                              SelectionOperator selectionOperator,
                              SolutionListEvaluator evaluator, List interestPoint,
                              double epsilon) {
        super(problem, 25000, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator,
                selectionOperator, evaluator, interestPoint, epsilon);
    }


    @Override
    public void restart(RestartStrategy restartStrategy) {
        restartStrategy.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
        this.evaluate(getPopulation());
        this.initProgress();
    }

    @Override
    public void compute() {
        matingPopulation = selection(this.getPopulation());
        offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        setPopulation(replacement(this.getPopulation(), offspringPopulation));
    }

    @Override
    public List<S> initializePopulation() {
        setPopulation(createInitialPopulation());
        return getPopulation();
    }

    @Override
    public void evaluate(List<S> population) {
        setPopulation(this.evaluatePopulation(population));
    }

}
