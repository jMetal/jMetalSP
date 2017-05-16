package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.algorithm.indm2.InDM2Builder;
import org.uma.jmetalsp.algorithm.wasfga.DynamicWASFGABuilder;
import org.uma.jmetalsp.consumer.ChartInDM2Consumer;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingDataSourceFromKeyboard;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ListObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.fda.FDA2;

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
public class InDM2Runner {

  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            SingleObservedData<Integer>,
            AlgorithmObservedData,
            DynamicProblem<DoubleSolution, SingleObservedData<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData, Observable<AlgorithmObservedData>>,
            SimpleStreamingCounterDataSource,
            AlgorithmDataConsumer<AlgorithmObservedData, DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData, Observable<AlgorithmObservedData>>>> application;
    application = new JMetalSPApplication<>();

    // Set the streaming data source for the problem
    Observable<SingleObservedData<Integer>> fdaObservable = new DefaultObservable<>("timeData");
    StreamingDataSource<SingleObservedData<Integer>, Observable<SingleObservedData<Integer>>> streamingDataSource =
            new SimpleStreamingCounterDataSource(fdaObservable, 2000);

    // Set the streaming data source for the algorithm
    Observable<ListObservedData<Double>> algorithmObservable = new DefaultObservable<>("Problem observable");
    StreamingDataSource<ListObservedData<Double>, Observable<ListObservedData<Double>>> streamingDataSource2 =
            new SimpleStreamingDataSourceFromKeyboard(algorithmObservable) ;

    // Problem configuration
    DynamicProblem<DoubleSolution, SingleObservedData<Integer>> problem = new FDA2(fdaObservable);

    // Algorithm configuration
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData, Observable<AlgorithmObservedData>> algorithm;
    Observable<AlgorithmObservedData> observable = new DefaultObservable<>("InDM2");


    List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.5);
    referencePoint.add(0.5);

    algorithm = new InDM2Builder<>(crossover, mutation, referencePoint, observable)
            .setMaxIterations(500)
            .setPopulationSize(100)
            .build(problem);

    application.setStreamingRuntime(new DefaultRuntime<SingleObservedData<Integer>, Observable<SingleObservedData<Integer>>, SimpleStreamingCounterDataSource>())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource)
            .addStreamingDataSource(streamingDataSource2)
            .addAlgorithmDataConsumer(new ChartInDM2Consumer(algorithm, referencePoint))
            .run();
  }
}
