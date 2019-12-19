package org.uma.jmetalsp.examples.continuousproblemapplication;

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
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIAVRO;
import org.uma.jmetalsp.consumer.ChartConsumerAVRO;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.KafkaBasedConsumer;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.qualityindicator.CoverageFront;
import org.uma.jmetalsp.serialization.algorithmdata.AlgorithmData;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNSolutionsAccordingToTheHypervolumeContribution;

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
public class DynamicContinuousApplicationAVRO {

  public static void main(String[] args) throws IOException, InterruptedException {
    // STEP 1. Create the problem
    DynamicProblem<DoubleSolution, ObservedValue<Integer>> problem =
            new FDA2();

    // STEP 2. Create the algorithm


    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection=new BinaryTournamentSelection<DoubleSolution>();
    InvertedGenerationalDistance<PointSolution> igd =
            new InvertedGenerationalDistance<>();
    CoverageFront<PointSolution> coverageFront = new CoverageFront<>(0.005,igd);
    Observable<AlgorithmData> observable = new KafkaObservable<>("front","avsc/AlgorithmData.avsc");
    DynamicAlgorithm<List<DoubleSolution>, AlgorithmData> algorithm =
            new DynamicNSGAIIAVRO<DoubleSolution>(problem,250000, 100, crossover, mutation,
                    selection, new SequentialSolutionListEvaluator<>(), new DominanceComparator<>(),observable,coverageFront);

    algorithm.setRestartStrategy(new RestartStrategy<>(
            //new RemoveFirstNSolutions<>(50),
            new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
            //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
            //new RemoveNRandomSolutions(50),
            new CreateNRandomSolutions<DoubleSolution>()));



    // STEP 3. Create the streaming data source (only one in this example) and register the problem
    StreamingDataSource<ObservedValue<Integer>> streamingDataSource =
            new SimpleStreamingCounterDataSource(2000) ;

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

    application.setStreamingRuntime(new DefaultRuntime())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource,problem)
            //.addAlgorithmDataConsumer(localDirectoryOutputConsumer)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();
  }
}
