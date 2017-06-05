package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetalsp.AlgorithmDataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCellBuilder;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.algorithm.smpso.DynamicSMPSOBuilder;
import org.uma.jmetalsp.JMetalSPApplication;
import org.uma.jmetalsp.algorithm.wasfga.DynamicWASFGABuilder;
import org.uma.jmetalsp.consumer.ChartConsumer;
import org.uma.jmetalsp.consumer.SimpleSolutionListConsumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData2;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of SparkSP application.
 * Features:
 * - Algorithm: to choose among NSGA-II, SMPSO and MOCell
 * - Problem: Any of the FDA familiy
 * - Default streaming runtime (Spark is not used)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousApplication {

  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            SingleObservedData<Integer>,
            AlgorithmObservedData2,
            DynamicProblem<DoubleSolution, SingleObservedData<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, Observable<AlgorithmObservedData2>>,
            SimpleStreamingCounterDataSource,
            AlgorithmDataConsumer<AlgorithmObservedData2, DynamicAlgorithm<List<DoubleSolution>,
                    Observable<AlgorithmObservedData2>>>> application;
    application = new JMetalSPApplication<>();

    // Set the streaming data source
    Observable<SingleObservedData<Integer>> fdaObservable = new DefaultObservable<>("timeData") ;
    StreamingDataSource<SingleObservedData<Integer>, Observable<SingleObservedData<Integer>>> streamingDataSource =
            new SimpleStreamingCounterDataSource(fdaObservable, 2000) ;

    // Problem configuration
	  DynamicProblem<DoubleSolution, SingleObservedData<Integer>> problem = new FDA2(fdaObservable);

	  // Algorithm configuration
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    String defaultAlgorithm = "WASFGA";

    DynamicAlgorithm<List<DoubleSolution>, Observable<AlgorithmObservedData2>> algorithm;
    Observable<AlgorithmObservedData2> observable = new DefaultObservable<>("WASFGA") ;

    switch (defaultAlgorithm) {
      case "NSGAII":
        algorithm = new DynamicNSGAIIBuilder<>(crossover, mutation, observable)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;

      case "MOCell":
        algorithm = new DynamicMOCellBuilder<>(crossover, mutation, observable)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;

      case "SMPSO":
        algorithm = new DynamicSMPSOBuilder<>(
                mutation, new CrowdingDistanceArchive<>(100), observable)
                .setMaxIterations(500)
                .setSwarmSize(100)
                .build(problem);
        break;
      case "WASFGA":
        List<Double> referencePoint = new ArrayList<>();
        referencePoint.add(0.5);
        referencePoint.add(0.5);

        algorithm = new DynamicWASFGABuilder<>(crossover, mutation, referencePoint, observable)
                .setMaxIterations(500)
                .setPopulationSize(100)
                .build(problem);
        break;

      default:
        algorithm = null;
    }

    application.setStreamingRuntime(new DefaultRuntime<SingleObservedData<Integer>, Observable<SingleObservedData<Integer>>, SimpleStreamingCounterDataSource>())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource)
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer(algorithm))
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory", algorithm))
            .addAlgorithmDataConsumer(new ChartConsumer(algorithm))
            .run();
  }
}
