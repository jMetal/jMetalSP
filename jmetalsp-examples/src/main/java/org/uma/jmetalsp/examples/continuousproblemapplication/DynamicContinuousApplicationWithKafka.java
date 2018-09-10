package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.consumer.ChartConsumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedIntegerValue;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.impl.KafkaBasedConsumer;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNRandomSolutions;

import java.io.IOException;
import java.util.List;

/**
 * Example of jMetalSP application.
 * Features:
 * - Algorithm: to choose among NSGA-II, SMPSO, MOCell, and WASF-GA
 * - Problem: Any of the FDA familiy
 * - Default streaming runtime (Spark is not used)
 *
 * Steps to compile and run the example:
 * 1. Compile the project:
     mvn package
 * 2. Run the program:
    java -cp jmetalsp-examples/target/jmetalsp-examples-1.1-SNAPSHOTar-with-dependencies.jar \
    org.uma.jmetalsp.examples.continuousproblemapplication.DynamicContinuousApplication

 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousApplicationWithKafka {

  public static void main(String[] args) throws IOException, InterruptedException {
    // STEP 1. Create the problem
	  DynamicProblem<DoubleSolution, ObservedValue<Integer>> problem =  new FDA2();

	  // STEP 2. Create the algorithm
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
      new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection=new BinaryTournamentSelection<DoubleSolution>();;

    DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData> algorithm =
      new DynamicNSGAIIBuilder<>(crossover, mutation, new KafkaObservable<>("topic-solutionlist-1", new AlgorithmObservedData()))
        .setMaxEvaluations(50000)
        .setPopulationSize(100)
        .build(problem);

    algorithm.setRestartStrategy(new RestartStrategy<>(
            //new RemoveFirstNSolutions<>(50),
            //new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
            //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
            new RemoveNRandomSolutions(15),
            new CreateNRandomSolutions<DoubleSolution>()));

    // STEP 3. Create the streaming data source (only one in this example) and register the problem
    StreamingDataSource<ObservedValue<Integer>> streamingDataSource =
      new SimpleStreamingCounterDataSource(
        new KafkaObservable<>("topic-int-1", new ObservedValue<>()), 2000) ;

    streamingDataSource.getObservable().register(problem);

    // STEP 4. Create the data consumers and register into the algorithm
    DataConsumer<AlgorithmObservedData> localDirectoryOutputConsumer =
            new LocalDirectoryOutputConsumer<DoubleSolution>("outputdirectory") ;
    DataConsumer<AlgorithmObservedData> chartConsumer =
            new ChartConsumer<DoubleSolution>() ;

    // STEP 5. Create the application and run
    JMetalSPApplication<
            DoubleSolution,
            DynamicProblem<DoubleSolution, ObservedValue<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData>> application;

    application = new JMetalSPApplication<>();

    // STEP 6. Create the kafka consumers (associated to the data consumers) and start them
    KafkaBasedConsumer<ObservedValue<Integer>> problemKafkaBasedConsumer =
      new KafkaBasedConsumer<>("topic-int-1", problem, new ObservedValue<>()) ;
    KafkaBasedConsumer<AlgorithmObservedData> chartKafkaBasedConsumer =
      new KafkaBasedConsumer<>("topic-solutionlist-1", chartConsumer, new AlgorithmObservedData()) ;
    KafkaBasedConsumer<AlgorithmObservedData> localDirectoryConsumer =
      new KafkaBasedConsumer<>("topic-solutionlist-1", localDirectoryOutputConsumer, new AlgorithmObservedData()) ;

    problemKafkaBasedConsumer.start();
    chartKafkaBasedConsumer.start();
    localDirectoryConsumer.start();

    application.setStreamingRuntime(new DefaultRuntime())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource,problem)
            .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();
  }
}
