package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCellBuilder;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.algorithm.nsgaiii.DynamicNSGAIIIBuilder;
import org.uma.jmetalsp.algorithm.rnsgaii.DynamicRNSGAIIBuilder;
import org.uma.jmetalsp.algorithm.smpso.DynamicSMPSOBuilder;
import org.uma.jmetalsp.algorithm.wasfga.DynamicWASFGABuilder;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.qualityindicator.CoverageFront;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 23/06/17.
 */
public class AlgorithmFactory {

  public static DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData>
  getAlgorithm(String algorithmName, DynamicProblem<DoubleSolution, ObservedValue<Integer>> problem) {
    DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData> algorithm;

    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection=new BinaryTournamentSelection<DoubleSolution>();
    InvertedGenerationalDistance<PointSolution> igd =
            new InvertedGenerationalDistance<>();
    CoverageFront<PointSolution> coverageFront = new CoverageFront<>(0.005,igd);

    switch (algorithmName) {
      case "NSGAII":
        coverageFront = new CoverageFront<>(0.05,igd);
        algorithm = new DynamicNSGAIIBuilder<>(crossover, mutation, new DefaultObservable<>(),coverageFront)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .setAutoUpdate(false)
                .build(problem);
        break;

      case "MOCell":
        coverageFront = new CoverageFront<>(0.05,igd);
        algorithm = new DynamicMOCellBuilder<>(crossover, mutation, new DefaultObservable<>(),coverageFront)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;
      case "SMPSO":
        algorithm = new DynamicSMPSOBuilder<>(
                mutation, new CrowdingDistanceArchive<>(100), new DefaultObservable<>(),coverageFront)
                .setMaxIterations(500)
                .setSwarmSize(100)
                .build(problem);
        break;
      case "WASFGA":
        List<Double> referencePoint = new ArrayList<>();
        referencePoint.add(0.5);
        referencePoint.add(0.5);

        algorithm = new DynamicWASFGABuilder<>(crossover, mutation, referencePoint,0.005, new DefaultObservable<>(),coverageFront)
                .setMaxIterations(500)
                .setPopulationSize(100)
                .build(problem);
        break;


      case "NSGAIII":
        algorithm = (DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData>) new DynamicNSGAIIIBuilder<>(problem,new DefaultObservable<>(),coverageFront)
                .setCrossoverOperator(crossover)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(50000)
                .build();

        break;
      case "RNSGAII":
        coverageFront = new CoverageFront<>(0.15,igd);
        List<Double> interestPoint = new ArrayList<>();
        interestPoint.add(0.5);
        interestPoint.add(0.5);
        double epsilon = 0.001D;
        algorithm = (DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData>)
                new DynamicRNSGAIIBuilder<>(crossover, mutation, new DefaultObservable<>(),interestPoint,epsilon,coverageFront)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);

        break;
      default:
        throw new JMetalException("Algorithm " + algorithmName + " does not exist") ;
    }

    return algorithm ;
  }
}
