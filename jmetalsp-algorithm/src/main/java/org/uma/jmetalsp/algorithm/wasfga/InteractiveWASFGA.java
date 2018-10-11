package org.uma.jmetalsp.algorithm.wasfga;

import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
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

public class InteractiveWASFGA <S extends Solution<?>> extends WASFGA<S> implements
    InteractiveAlgorithm<S,List<S>> {
  List<S> offspringPopulation;
  List<S> matingPopulation;

  public InteractiveWASFGA(Problem<S> problem, int populationSize,

      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator, double epsilon,
      List<Double> referencePoint, String weightVectorsFileName) {
    super(problem, populationSize, 25000, crossoverOperator, mutationOperator,
        selectionOperator, evaluator, epsilon, referencePoint, weightVectorsFileName);
  }

  public InteractiveWASFGA(Problem<S> problem, int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator, double epsilon,
      List<Double> referencePoint) {
    super(problem, populationSize, 25000, crossoverOperator, mutationOperator,
        selectionOperator, evaluator, epsilon, referencePoint);
  }

  @Override
  public void restart(RestartStrategy restartStrategy) {
   restartStrategy.restart(getPopulation(), (DynamicProblem<S, ?>)getProblem());
    this.evaluatePopulation(this.getPopulation());
    this.initProgress();
    this.specificMOEAComputations();
  }

  @Override
  public void compute() {
    matingPopulation = selection(this.getPopulation());
    offspringPopulation = reproduction(matingPopulation);
    offspringPopulation = evaluatePopulation(offspringPopulation);
    this.setPopulation(replacement(this.getPopulation(), offspringPopulation));
    // specific GA needed computations
    this.specificMOEAComputations();
  }

  @Override
  public List<S> initializePopulation() {
    this.setPopulation(createInitialPopulation());
    this.evaluatePopulation(this.getPopulation());
    this.specificMOEAComputations();
    return this.getPopulation();
  }

  @Override
  public void evaluate(List<S> population) {
    this.setPopulation( this.evaluatePopulation(population));
  }


}
