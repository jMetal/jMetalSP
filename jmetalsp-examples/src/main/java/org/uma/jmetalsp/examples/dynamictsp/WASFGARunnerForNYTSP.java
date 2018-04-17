package org.uma.jmetalsp.examples.dynamictsp;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetalsp.*;

import org.uma.jmetalsp.algorithm.wasfga.DynamicWASFGA;
import org.uma.jmetalsp.algorithm.wasfga.DynamicWASFGABuilder;

import org.uma.jmetalsp.consumer.ChartMultipleConsumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.streamingdatasource.ComplexStreamingDataSourceFromKeyboard;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observer;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromNYData;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNSolutionsAccordingToTheHypervolumeContribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of SparkSP application.
 * Features:
 * - Algorithm: InDM2
 * - Problem: Bi-objective TSP (using data files from TSPLIB)
 * - Default streaming runtime (Spark is not used)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class WASFGARunnerForNYTSP {

  public static void main(String[] args) throws IOException, InterruptedException {
    // STEP 1. Create the problem
    DynamicProblem<PermutationSolution<Integer>, SingleObservedData<TSPMatrixData>> problem;
    problem = new MultiobjectiveTSPBuilderFromNYData("data/nyData.txt")
            .build();

    // STEP 2. Create and configure the algorithm
    List<Double> referencePoint = new ArrayList<>();

    /*referencePoint.add(160000.0);
    referencePoint.add(9500.0);
    referencePoint.add(167000.0);
    referencePoint.add(9400.0);
    referencePoint.add(168500.0);
    referencePoint.add(9300.0);
    referencePoint.add(169000.0);
    referencePoint.add(9200.0);*/

    /*referencePoint.add(0.0);
    referencePoint.add(0.0);
    referencePoint.add(2000.0);
    referencePoint.add(2000.0);
    referencePoint.add(10000.0);
    referencePoint.add(7000.0);
    referencePoint.add(16000.0);
    referencePoint.add(9000.0);*/


    referencePoint.add(15000.0);
    referencePoint.add(5000.0);
    /*
    referencePoint.add(171000.0);
    referencePoint.add(11000.0);
    referencePoint.add(172000.0);
    referencePoint.add(10400.0);

    referencePoint.add(172500.0);
    referencePoint.add(10000.0);

    referencePoint.add(174000.0);
    referencePoint.add(9000.0);

    referencePoint.add(175000.0);
    referencePoint.add(8900.0);

    referencePoint.add(176000.0);
    referencePoint.add(8700.0);*/

    ////171000.0,11000.0,172000.0,10400.0,172500.0,10000.0,174000.0,9000.0,175000.0,8900.0,176000.0,8700.0


    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;

    crossover = new PMXCrossover(0.9);

    double mutationProbability = 0.2;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability);

    double epsilon = 0.001D;

    DynamicWASFGA<PermutationSolution<Integer>> algorithm = new DynamicWASFGABuilder<>(crossover, mutation,referencePoint,0.005, new DefaultObservable<>())
    //DynamicNSGAII<PermutationSolution<Integer>> algorithm =  algorithm = new DynamicNSGAIIBuilder<>(crossover, mutation, new DefaultObservable<>())
            .setMaxIterations(2500)
            .setPopulationSize(100)
            .build(problem);

    algorithm.setRestartStrategy(new RestartStrategy<>(
            //new RemoveFirstNSolutions<>(50),
            new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
            //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
            //new RemoveNRandomSolutions(50),
            new CreateNRandomSolutions<>()));

    //algorithm.setRestartStrategyForReferencePointChange(new RestartStrategy<>(
     //       new RemoveNRandomSolutions<>(100),
     //       new CreateNRandomSolutions<PermutationSolution<Integer>>()));

    // STEP 3. Create a streaming data source for the problem and register
    StreamingTSPFileSource streamingTSPSource = new StreamingTSPFileSource(new DefaultObservable<>(), 10000);

    streamingTSPSource.getObservable().register(problem);

    // STEP 4. Create a streaming data source for the algorithm and register
    StreamingDataSource<SingleObservedData<List<Double>>> keyboardstreamingDataSource =
            new ComplexStreamingDataSourceFromKeyboard() ;

    //keyboardstreamingDataSource.getObservable().register(algorithm);

    // STEP 5. Create the data consumers and register into the algorithm
    DataConsumer<AlgorithmObservedData<PermutationSolution<Integer>>> localDirectoryOutputConsumer =
            new LocalDirectoryOutputConsumer<PermutationSolution<Integer>>("outputdirectory");
    DataConsumer<AlgorithmObservedData<PermutationSolution<Integer>>> chartConsumer =
            new ChartMultipleConsumer<PermutationSolution<Integer>>(algorithm,referencePoint,problem.getNumberOfObjectives());

    algorithm.getObservable().register(localDirectoryOutputConsumer);
    algorithm.getObservable().register(chartConsumer) ;

    // STEP 6. Create the application and run
    JMetalSPApplication<
            PermutationSolution<Integer>,
            DynamicProblem<PermutationSolution<Integer>, SingleObservedData<Integer>>,
            DynamicAlgorithm<List<PermutationSolution<Integer>>, AlgorithmObservedData<PermutationSolution<Integer>>>> application;

    application = new JMetalSPApplication<>();

    application.setStreamingRuntime(new DefaultRuntime())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingTSPSource,problem)
            .addStreamingDataSource(keyboardstreamingDataSource,algorithm)
            .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();
  }
}
