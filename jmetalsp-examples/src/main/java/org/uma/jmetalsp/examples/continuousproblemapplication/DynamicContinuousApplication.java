package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.consumer.ChartConsumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.problem.fda.FDA2;

import java.io.IOException;
import java.util.List;

/**
 * Example of jMetalSP application.
 * Features:
 * - Algorithm: to choose among NSGA-II, SMPSO, MOCell, and WASF-GA
 * - Problem: Any of the FDA familiy
 * - Default streaming runtime (Spark is not used)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousApplication {

  public static void main(String[] args) throws IOException, InterruptedException {
    // STEP 1. Create the problem
	  DynamicProblem<DoubleSolution, SingleObservedData<Integer>> problem =
            new FDA2();

	  // STEP 2. Create the algorithm
    DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData<DoubleSolution>> algorithm =
            AlgorithmFactory.getAlgorithm("WASFGA", problem) ;

    // STEP 3. Create the streaming data source (only one in this example) and register the problem
    StreamingDataSource<SingleObservedData<Integer>> streamingDataSource =
            new SimpleStreamingCounterDataSource(2000) ;

    streamingDataSource.getObservable().register(problem);

    // STEP 4. Create the data consumers and register into the algorithm
    DataConsumer<AlgorithmObservedData<DoubleSolution>> localDirectoryOutputConsumer =
            new LocalDirectoryOutputConsumer<DoubleSolution>("outputdirectory", algorithm) ;
    DataConsumer<AlgorithmObservedData<DoubleSolution>> chartConsumer =
            new ChartConsumer<DoubleSolution>(algorithm) ;

    algorithm.getObservable().register(localDirectoryOutputConsumer);
    algorithm.getObservable().register(chartConsumer) ;

    // STEP 5. Create the application and run
    JMetalSPApplication<
            DoubleSolution,
            DynamicProblem<DoubleSolution, SingleObservedData<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData<DoubleSolution>>> application;

    application = new JMetalSPApplication<>();

    application.setStreamingRuntime(new DefaultRuntime())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource)
            .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();
  }
}
