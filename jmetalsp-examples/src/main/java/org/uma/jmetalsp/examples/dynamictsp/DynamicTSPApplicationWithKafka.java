package org.uma.jmetalsp.examples.dynamictsp;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.consumer.ChartConsumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.observer.impl.KafkaBasedConsumer;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromNYData;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;
import org.uma.jmetalsp.spark.SparkRuntime;
import org.uma.jmetalsp.spark.streamingdatasource.SimpleSparkStructuredKafkaStreamingTSP;
import org.uma.jmetalsp.streamingdatasource.SimpleKafkaStreamingTSPDataSource;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNRandomSolutions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class DynamicTSPApplicationWithKafka {

  public static void main(String[] args) throws IOException, InterruptedException {
    // STEP 1. Create the problem
    DynamicProblem<PermutationSolution<Integer>, ObservedValue<TSPMatrixData>> problem;
    // problem = new MultiobjectiveTSPBuilderFromTSPLIBFiles("data/kroA100.tsp", "data/kroB100.tsp")
    //       .build();
    problem = new MultiobjectiveTSPBuilderFromNYData("data/nyData.txt").build() ;

    // STEP 2. Create the algorithm
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

    crossover = new PMXCrossover(0.9);

    double mutationProbability = 0.2;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability);

    selection = new BinaryTournamentSelection<>(
            new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    DynamicAlgorithm<List<PermutationSolution<Integer>>, AlgorithmObservedData> algorithm;
    algorithm = new DynamicNSGAIIBuilder<>(crossover, mutation, new DefaultObservable<>())
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setSelectionOperator(selection)
            .build(problem);

    algorithm.setRestartStrategy(new RestartStrategy<>(
            //new RemoveFirstNSolutions<>(50),
            //new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
            //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
            new RemoveNRandomSolutions(15),
            new CreateNRandomSolutions<DoubleSolution>()));

    // STEP 3. Create the streaming data source and register the problem
    String topic="tsp";
    Map<String,Object> kafkaParams = new HashMap<>();
    kafkaParams.put("bootstrap.servers", "localhost:9092");
    kafkaParams.put("key.deserializer", IntegerDeserializer.class);
    kafkaParams.put("value.deserializer", StringDeserializer.class);
    kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
    kafkaParams.put("auto.offset.reset", "latest");
    kafkaParams.put("enable.auto.commit", false);
   // SimpleSparkStructuredKafkaStreamingTSP streamingTSPSource =
    //        new SimpleSparkStructuredKafkaStreamingTSP(
    //                new KafkaObservable<ObservedValue<TSPMatrixData>>("matrix-topic","src/main/resources/counter.avsc"),kafkaParams, topic);
    SimpleKafkaStreamingTSPDataSource streamingTSPSource =
            new SimpleKafkaStreamingTSPDataSource(
                    new KafkaObservable<ObservedValue<TSPMatrixData>>("matrix-topic","src/main/resources/counter.avsc"));
    streamingTSPSource.setTopic(topic);
    streamingTSPSource.getObservable().register(problem);
    //StreamingDataSource<ObservedValue<String>> streamingDataSource =
     //       new SimpleSparkStructuredKafkaStreamingTSP(
      //              new KafkaObservable<>(kafkaParams, topic) ;

//streamingDataSource.getObservable().register(problem);
    // STEP 4. Create the data consumers and register into the algorithm
    DataConsumer<AlgorithmObservedData> localDirectoryOutputConsumer =
            new LocalDirectoryOutputConsumer<DoubleSolution>("outputdirectory") ;
    DataConsumer<AlgorithmObservedData> chartConsumer =
            new ChartConsumer<DoubleSolution>() ;

    // STEP 5. Create the application and run
    JMetalSPApplication<
            PermutationSolution<Integer>,
            DynamicProblem<PermutationSolution<Integer>, ObservedValue<Integer>>,
            DynamicAlgorithm<List<PermutationSolution<Integer>>, AlgorithmObservedData>> application;

    application = new JMetalSPApplication<>();



    // STEP 6. Create the kafka consumers (associated to the data consumers) and start them
    KafkaBasedConsumer<ObservedValue<TSPMatrixData>> problemKafkaBasedConsumer =
      new KafkaBasedConsumer<>("matrix-topic", problem, new ObservedValue<>()) ;
    KafkaBasedConsumer<AlgorithmObservedData> chartKafkaBasedConsumer =
      new KafkaBasedConsumer<>("topic-solutionlist-1", chartConsumer, new AlgorithmObservedData()) ;
    KafkaBasedConsumer<AlgorithmObservedData> localDirectoryConsumer =
      new KafkaBasedConsumer<>("topic-solutionlist-1", localDirectoryOutputConsumer, new AlgorithmObservedData()) ;

    problemKafkaBasedConsumer.start();
    chartKafkaBasedConsumer.start();
    localDirectoryConsumer.start();

    application.setStreamingRuntime(new KafkaRuntime(topic))
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingTSPSource,problem)
            .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();
  }
}
