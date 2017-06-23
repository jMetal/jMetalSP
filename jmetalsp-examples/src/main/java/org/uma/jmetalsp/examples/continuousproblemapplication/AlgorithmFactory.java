package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCellBuilder;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.algorithm.smpso.DynamicSMPSOBuilder;
import org.uma.jmetalsp.algorithm.wasfga.DynamicWASFGABuilder;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 23/06/17.
 */
public class AlgorithmFactory {

  public static DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData<DoubleSolution>>
  getAlgorithm(String algorithmName, DynamicProblem<DoubleSolution, SingleObservedData<Integer>> problem) {
    DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData<DoubleSolution>> algorithm;

    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    switch (algorithmName) {
      case "NSGAII":
        algorithm = new DynamicNSGAIIBuilder<>(crossover, mutation, new DefaultObservable<>())
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;

      case "MOCell":
        algorithm = new DynamicMOCellBuilder<>(crossover, mutation, new DefaultObservable<>())
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;
      case "SMPSO":
        algorithm = new DynamicSMPSOBuilder<>(
                mutation, new CrowdingDistanceArchive<>(100), new DefaultObservable<>())
                .setMaxIterations(500)
                .setSwarmSize(100)
                .build(problem);
        break;
      case "WASFGA":
        List<Double> referencePoint = new ArrayList<>();
        referencePoint.add(0.0);
        referencePoint.add(0.0);

        algorithm = new DynamicWASFGABuilder<>(crossover, mutation, referencePoint, new DefaultObservable<>())
                .setMaxIterations(500)
                .setPopulationSize(100)
                .build(problem);
        break;

      default:
        throw new JMetalException("Algorithm " + algorithmName + " does not exist") ;
    }

    return algorithm ;
  }
}
