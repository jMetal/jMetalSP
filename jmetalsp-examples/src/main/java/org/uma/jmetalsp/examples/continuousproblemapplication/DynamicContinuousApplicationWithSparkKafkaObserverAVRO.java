package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.spark.SparkConf;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.JMetalSPApplication;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIAVRO;
import org.uma.jmetalsp.consumer.ChartConsumerAVRO;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.KafkaBasedConsumer;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.qualityindicator.CoverageFront;
import org.uma.jmetalsp.serialization.algorithmdata.AlgorithmData;
import org.uma.jmetalsp.spark.SparkRuntime;
import org.uma.jmetalsp.spark.SparkStreamingDataSource;
import org.uma.jmetalsp.spark.streamingdatasource.SimpleSparkStructuredKafkaStreamingCounterAVRO;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNSolutionsAccordingToTheHypervolumeContribution;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example of jMetalSP application.
 * Features:
 * - Algorithm: to choose among NSGA-II, SMPSO, MOCell, and WASF-GA
 * - Problem: Any of the FDA familiy
 * - Spark streaming runtime
 *
 * Steps to compile and run the example:
 * 1. Compile the project:
      mvn package
 * 2. Run the program with the name of the output directory where the fronts will be stored:
      spark-submit --class="org.uma.jmetalsp.examples.continuousproblemapplication.DynamicContinuousApplicationWithSpark" \
      jmetalsp-examples/target/jmetalsp-examples-1.1-SNAPSHOT-jar-with-dependencies.jar outputDirectory
 * 3. At the same time, run the program to simulate the streaming data source that generates time:
      java -cp jmetalsp-externalsource/target/jmetalsp-externalsource-1-SNAPSHOT-jar-with-dependencies.jar \
      org.uma.jmetalsp.externalsources.CounterProvider outputDirectory 2000
 * where "outputDirectory" must the same used in Step 3, and the second argument is the frequency of
 * data generation (2000 milliseconds in this example)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousApplicationWithSparkKafkaObserverAVRO {

  public static void main(String[] args) throws IOException, InterruptedException {
    // STEP 1. Create the problem
    DynamicProblem<DoubleSolution, ObservedValue<Integer>> problem =
            new FDA2();

    // STEP 2. Create the algorithm


    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection=new BinaryTournamentSelection<DoubleSolution>();
    Observable<AlgorithmData> observable = new KafkaObservable<>("front","avsc/AlgorithmData.avsc");
    InvertedGenerationalDistance<PointSolution> igd =
            new InvertedGenerationalDistance<>();
    CoverageFront<PointSolution> coverageFront = new CoverageFront<>(0.005,igd);
    DynamicAlgorithm<List<DoubleSolution>, AlgorithmData> algorithm =
           new  DynamicNSGAIIAVRO<DoubleSolution>(problem,25000, 100, crossover, mutation,
    selection, new SequentialSolutionListEvaluator<>(), new DominanceComparator<>(),observable,coverageFront);

    algorithm.setRestartStrategy(new RestartStrategy<>(
            //new RemoveFirstNSolutions<>(50),
            new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
            //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
            //new RemoveNRandomSolutions(50),
            new CreateNRandomSolutions<DoubleSolution>()));

    // STEP 3. Create the streaming data source (only one in this example) and register the problem
    String topic="counter";
    Map<String,Object>  kafkaParams = new HashMap<>();
    kafkaParams.put("bootstrap.servers", "192.168.227.26:9092");
    kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
    kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    kafkaParams.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    kafkaParams.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
    kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");

    SparkStreamingDataSource streamingDataSource =
            new SimpleSparkStructuredKafkaStreamingCounterAVRO(kafkaParams,topic) ;

    // STEP 4. Create the data consumers and register into the algorithm

    DataConsumer<AlgorithmData> chartConsumer =
            new ChartConsumerAVRO<>(algorithm.getName()) ;

    KafkaBasedConsumer<AlgorithmData> chartKafkaBasedConsumer =
            new KafkaBasedConsumer<>("front", chartConsumer, new AlgorithmData(),"avsc/AlgorithmData.avsc") ;
    chartKafkaBasedConsumer.start();

    // STEP 5. Create the application and run
    JMetalSPApplication<
            DoubleSolution,
            DynamicProblem<DoubleSolution, ObservedValue<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, AlgorithmData>> application;

    application = new JMetalSPApplication<>();

    String sparkHomeDirectory =args[0];
    SparkConf sparkConf = new SparkConf()
            .setAppName("SparkApp")
            .setSparkHome(sparkHomeDirectory)
            .setMaster("local[4]") ;
    application.setStreamingRuntime(new SparkRuntime(2,sparkConf))
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource,problem)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();

  }
}
