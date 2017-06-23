package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.JMetalSPApplication;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCellBuilder;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.algorithm.smpso.DynamicSMPSOBuilder;
import org.uma.jmetalsp.algorithm.wasfga.DynamicWASFGABuilder;
import org.uma.jmetalsp.consumer.ChartConsumer;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.impl.DefaultRuntime;

import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.fda.FDA2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of jMetalSP application.
 * Features:
 * - Algorithm: to choose among NSGA-II, SMPSO MOCell, and WAFG-GA
 * - Problem: Any of the FDA familiy
 * - Default streaming runtime (Spark is not used)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousApplicationWithCharts {

  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            DoubleSolution,
            DynamicProblem<DoubleSolution, SingleObservedData<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, Observable<AlgorithmObservedData<DoubleSolution>>>> application;
    application = new JMetalSPApplication<>();

    // STEP 1. Create observable entities
    // STEP 1.1. Observable to be attached to a streaming data source
    Observable<SingleObservedData<Integer>> streamingObservable = new DefaultObservable<>("timeData") ;

    // STEP 1.1. Observable to be attached to the algorithm
    Observable<AlgorithmObservedData<DoubleSolution>> algorithmObservable = new DefaultObservable<>("algorithm") ;

    // STEP 2. Create the streaming data source (only one in this example)
    StreamingDataSource<Observable<SingleObservedData<Integer>>> streamingDataSource =
            new SimpleStreamingCounterDataSource(streamingObservable, 2000) ;

    // STEP 3. Create the problem, which has the streaming data source object as a parameter
    DynamicProblem<DoubleSolution, SingleObservedData<Integer>> problem = new FDA2(streamingObservable);

    // STEP 4. Algorithm configuration
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    DynamicAlgorithm<List<DoubleSolution>, Observable<AlgorithmObservedData<DoubleSolution>>> algorithm;

    String defaultAlgorithm = "WASFGA";

    switch (defaultAlgorithm) {
      case "NSGAII":
        algorithm = new DynamicNSGAIIBuilder<>(crossover, mutation, algorithmObservable)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;

      case "MOCell":
        algorithm = new DynamicMOCellBuilder<>(crossover, mutation, algorithmObservable)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;

      case "SMPSO":
        algorithm = new DynamicSMPSOBuilder<>(
                mutation, new CrowdingDistanceArchive<>(100), algorithmObservable)
                .setMaxIterations(500)
                .setSwarmSize(100)
                .build(problem);
        break;
      case "WASFGA":
        List<Double> referencePoint = new ArrayList<>();
        referencePoint.add(0.5);
        referencePoint.add(0.5);

        algorithm = new DynamicWASFGABuilder<>(crossover, mutation, referencePoint, algorithmObservable)
                .setMaxIterations(500)
                .setPopulationSize(100)
                .build(problem);
        break;

      default:
        algorithm = null;
    }

    application.setStreamingRuntime(new DefaultRuntime())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource)
            .addAlgorithmDataConsumer(new ChartConsumer(algorithm))
            .run();
  }
}
