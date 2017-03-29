package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCellBuilder;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.algorithm.smpso.DynamicSMPSOBuilder;
import org.uma.jmetalsp.JMetalSPApplication;
import org.uma.jmetalsp.consumer.SimpleSolutionListConsumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.updatedata.TimeObservedData;
import org.uma.jmetalsp.updatedata.impl.DefaultAlgorithmObservedData;
import org.uma.jmetalsp.perception.Observable;
import org.uma.jmetalsp.perception.impl.DefaultObservable;

import java.io.IOException;
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
            TimeObservedData,
            DynamicProblem<DoubleSolution, TimeObservedData>,
            DynamicAlgorithm<List<DoubleSolution>,TimeObservedData>,
            StreamingFDADataSource> application;
    application = new JMetalSPApplication<>();

    // Set the streaming data source
    Observable<TimeObservedData> fdaUpdateDataObservable = new DefaultObservable<>("timeData") ;
    StreamingDataSource<?, ?> streamingDataSource = new StreamingFDADataSource(fdaUpdateDataObservable, 2000) ;

    // Problem configuration
	  DynamicProblem<DoubleSolution, TimeObservedData> problem = new FDA2(fdaUpdateDataObservable);

	  // Algorithm configuration
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    String defaultAlgorithm = "SMPSO";

    DynamicAlgorithm<List<DoubleSolution>, DefaultAlgorithmObservedData> algorithm;
    Observable<DefaultAlgorithmObservedData> observable = new DefaultObservable<>("") ;

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

      default:
        algorithm = null;
    }

    application.setStreamingRuntime(new DefaultRuntime<TimeObservedData, StreamingFDADataSource>())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource)
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory"))
            .run();
  }
}
