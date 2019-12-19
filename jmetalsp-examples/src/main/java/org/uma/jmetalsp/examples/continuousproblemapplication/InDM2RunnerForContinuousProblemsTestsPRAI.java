package org.uma.jmetalsp.examples.continuousproblemapplication;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.algorithm.indm2.InDM2;
import org.uma.jmetalsp.algorithm.indm2.InDM2Builder;
import org.uma.jmetalsp.algorithm.rnsgaii.InteractiveRNSGAII;
import org.uma.jmetalsp.algorithm.smpso.InteractiveSMPSORP;
import org.uma.jmetalsp.algorithm.wasfga.InteractiveWASFGA;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.streamingdatasource.ComplexStreamingDataSourceFromKeyboard;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.df.*;
import org.uma.jmetalsp.qualityindicator.CoverageFront;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNRandomSolutions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class InDM2RunnerForContinuousProblemsTestsPRAI {

  public static void main(String[] args) throws IOException, InterruptedException {


    // STEP 1. Create the problem
    DynamicProblem<DoubleSolution, ObservedValue<Integer>> problem =
            new DF1();
      InteractiveAlgorithm<DoubleSolution,List<DoubleSolution>> interactiveAlgorithm= null;

           // new FDA2();

    // STEP 2. Create and configure the algorithm
  /*  List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.0);
    referencePoint.add(0.0);

    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);*/
  if(args!=null && args[0]!=null){
      switch(args[0].toUpperCase()){
          case "DF1": problem = new DF1();
          break;
        case "DF2":problem = new DF2();
        break;
        case "DF3":problem = new DF3();
        break;
        case "DF4":problem = new DF4();
          break;
        case "DF5":problem = new DF5();
          break;
        case "DF6": problem = new DF6();
          break;
        case "DF7":problem = new DF7();
          break;
        case "DF8":problem = new DF8();
          break;
        case "DF9":problem = new DF9();
          break;
        case "DF10":problem = new DF10();
          break;
        case "DF11": problem = new DF11();
          break;
        case "DF12":problem = new DF12();
          break;
        case "DF13":problem = new DF13();
          break;
        case "DF14":problem = new DF14();
          break;
        default: problem = new DF1();
      }
  }

    List<Double> referencePoint=Arrays.asList(0.0, 0.0,0.0);
    List<List<Double>> referencePoints;
    referencePoints = new ArrayList<>();

    referencePoints.add(referencePoint);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int maxIterations = 750000;
    int swarmSize = 100;

    List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints = new ArrayList<>();

    for (int i = 0; i < referencePoints.size(); i++) {
      archivesWithReferencePoints.add(
              new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(
                      swarmSize/referencePoints.size(), referencePoints.get(i))) ;
    }


    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
      double epsilon = 0.001D;
      int delay =5000;
      if(args!=null && args[1]!=null){
        switch(args[1].toUpperCase()){
          case "WASFGA":
            interactiveAlgorithm = new InteractiveWASFGA<>(problem,100,crossover,mutation,
                    new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()), new SequentialSolutionListEvaluator<>(),0.01,referencePoint );
            delay =5000;
            break;
          case "RNSGAII":
            interactiveAlgorithm  = new InteractiveRNSGAII(problem,100,100,100,crossover,mutation,
                    new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()), new SequentialSolutionListEvaluator<>(),referencePoint,epsilon );
            delay =15000;
            break;
            default:
              interactiveAlgorithm = new InteractiveSMPSORP(problem,
                      swarmSize,
                      archivesWithReferencePoints,
                      referencePoints,
                      mutation,
                      maxIterations,
                      0.0, 1.0,
                      0.0, 1.0,
                      2.5, 1.5,
                      2.5, 1.5,
                      0.1, 0.1,
                      -1.0, -1.0,
                      new SequentialSolutionListEvaluator<>());
              delay =5000;

        }
      }








    InvertedGenerationalDistance<PointSolution> igd =
            new InvertedGenerationalDistance<>();
    CoverageFront<PointSolution> coverageFront = new CoverageFront<>(0.005,igd);

    InDM2<DoubleSolution> algorithm = new InDM2Builder<>(interactiveAlgorithm, new DefaultObservable<>(),coverageFront)
            .setMaxIterations(50000)
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
    StreamingDataSource<ObservedValue<Integer>> streamingDataSource =
            new SimpleStreamingCounterDataSource(delay) ;


    // STEP 4. Create a streaming data source for the algorithm and register
    StreamingDataSource<ObservedValue<List<Double>>> keyboardstreamingDataSource =
            new ComplexStreamingDataSourceFromKeyboard() ;


    // STEP 5. Create the data consumers and register into the algorithm
    DataConsumer<AlgorithmObservedData> localDirectoryOutputConsumer =
            new LocalDirectoryOutputConsumer<DoubleSolution>("outputdirectory-"+problem.getName()+"-"+algorithm.getName()+"-"+referenceName(referencePoint)) ;//algorithm
    //DataConsumer<AlgorithmObservedData> chartConsumer =
     //       new ChartInDM2Consumer<DoubleSolution>(algorithm.getName(), referencePoint,problem.getNumberOfObjectives(),problem.getName()) ;


    // STEP 6. Create the application and run
    JMetalSPApplication<
            DoubleSolution,
            DynamicProblem<DoubleSolution, ObservedValue<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData>> application;

    application = new JMetalSPApplication<>(problem,algorithm);

    application.setStreamingRuntime(new DefaultRuntime())
            .addStreamingDataSource(streamingDataSource,problem)
            .addStreamingDataSource(keyboardstreamingDataSource,algorithm)
            .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
          //  .addAlgorithmDataConsumer(chartConsumer)
            .run();
  }
  private static String referenceName(List<Double> referencePoint){
    String result="(";
    for (Double ref:referencePoint) {
      result += ref+",";
    }
    result= result.substring(0,result.length()-1);
    result +=")";
    return result;
  }
}
