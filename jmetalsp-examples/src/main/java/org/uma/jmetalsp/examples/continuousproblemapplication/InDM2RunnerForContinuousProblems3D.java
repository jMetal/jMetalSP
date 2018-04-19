package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.algorithm.indm2.InDM2;
import org.uma.jmetalsp.algorithm.indm2.InDM2Builder;
import org.uma.jmetalsp.algorithm.wasfga.InteractiveWASFGA;
import org.uma.jmetalsp.consumer.ChartInDM2Consumer;
import org.uma.jmetalsp.consumer.ChartInDM2Consumer3D;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.streamingdatasource.ComplexStreamingDataSourceFromKeyboard;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingDataSourceFromKeyboard;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.problem.fda.FDA5;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNRandomSolutions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of SparkSP application.
 * Features:
 * - Algorithm: InDM2
 * - Problem: Any of the FDA familiy
 * - Default streaming runtime (Spark is not used)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InDM2RunnerForContinuousProblems3D {

  public static void main(String[] args) throws IOException, InterruptedException {
    // STEP 1. Create the problem
    DynamicProblem<DoubleSolution, SingleObservedData<Integer>> problem =
            new FDA5();

    // STEP 2. Create and configure the algorithm
    List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.0);
    referencePoint.add(0.0);
    referencePoint.add(0.0);

    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
    String weightVectorsFileName ="MOEAD_Weights/W3D_100.dat";

    InteractiveAlgorithm<DoubleSolution,List<DoubleSolution>> iWasfga = new InteractiveWASFGA<>(problem,100,crossover,mutation,
        new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()), new SequentialSolutionListEvaluator<>(),0.005,referencePoint,weightVectorsFileName );


    InDM2<DoubleSolution> algorithm = new InDM2Builder<>(iWasfga, new DefaultObservable<>())
            .setMaxIterations(25000)
            .setPopulationSize(100)
            .build(problem);

    algorithm.setRestartStrategy(new RestartStrategy<>(
            //new RemoveFirstNSolutions<>(50),
            //new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
            //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
            new RemoveNRandomSolutions(50),
            new CreateNRandomSolutions<DoubleSolution>()));

    algorithm.setRestartStrategyForReferencePointChange(new RestartStrategy<>(
            new RemoveNRandomSolutions<>(50),
            new CreateNRandomSolutions<DoubleSolution>()));

    // STEP 3. Create a streaming data source for the problem and register
    StreamingDataSource<SingleObservedData<Integer>> streamingDataSource =
            new SimpleStreamingCounterDataSource(2000) ;

    streamingDataSource.getObservable().register(problem);

    // STEP 4. Create a streaming data source for the algorithm and register
    StreamingDataSource<SingleObservedData<List<Double>>> keyboardstreamingDataSource =
            new ComplexStreamingDataSourceFromKeyboard() ;

    keyboardstreamingDataSource.getObservable().register(algorithm);

    // STEP 5. Create the data consumers and register into the algorithm
    DataConsumer<AlgorithmObservedData<DoubleSolution>> localDirectoryOutputConsumer =
            new LocalDirectoryOutputConsumer<DoubleSolution>("outputdirectory") ;
    DataConsumer<AlgorithmObservedData<DoubleSolution>> chartConsumer =
            new ChartInDM2Consumer3D<DoubleSolution>(algorithm.getName(), referencePoint) ;

    algorithm.getObservable().register(localDirectoryOutputConsumer);
    algorithm.getObservable().register(chartConsumer) ;

    // STEP 6. Create the application and run
    JMetalSPApplication<
            DoubleSolution,
            DynamicProblem<DoubleSolution, SingleObservedData<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData<DoubleSolution>>> application;

    application = new JMetalSPApplication<>();

    application.setStreamingRuntime(new DefaultRuntime())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource,problem)
            .addStreamingDataSource(keyboardstreamingDataSource,algorithm)
            .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();
  }
}
