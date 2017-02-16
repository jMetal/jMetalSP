package org.uma.jmetalsp.examples.dynamictsp;

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
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCellBuilder;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.consumer.SimpleSolutionListConsumer2;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.continuousproblemapplication.StreamingFDADataSource;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.problem.tsp.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromFiles;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPUpdateData;
import org.uma.jmetalsp.streamingdatasource.impl.DefaultStreamingDataSource;
import org.uma.jmetalsp.streamingruntime.impl.DefaultRuntime;
import org.uma.jmetalsp.updatedata.AlgorithmData;
import org.uma.jmetalsp.util.Observable;
import org.uma.jmetalsp.util.impl.DefaultObservable;

import java.io.IOException;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicTSPApplication {

  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            MultiobjectiveTSPUpdateData,
            DynamicProblem<DoubleSolution, MultiobjectiveTSPUpdateData>,
            DynamicAlgorithm<List<PermutationSolution<Integer>>,MultiobjectiveTSPUpdateData>,
            //StreamingFDADataSource> application;
            ?> application;
    application = new JMetalSPApplication<>();

	  // Problem configuration
    Observable<MultiobjectiveTSPUpdateData> dataObservable = new DefaultObservable<>("") ;
	  DynamicProblem<PermutationSolution<Integer>, MultiobjectiveTSPUpdateData> problem ;
	  problem = new MultiobjectiveTSPBuilderFromFiles("kroA100.tsp", "kroB100.tsp")
            .build(dataObservable) ;

	  // Algorithm configuration
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;
    crossover = new PMXCrossover(0.9) ;
    double mutationProbability = 0.2 ;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;
    selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    String defaultAlgorithm = "NSGAII";

    DynamicAlgorithm<List<PermutationSolution<Integer>>, AlgorithmData> algorithm;
    Observable<AlgorithmData> observable = new DefaultObservable<>("NSGAII") ;

    switch (defaultAlgorithm) {
      case "NSGAII":
        algorithm = new DynamicNSGAIIBuilder<
                PermutationSolution<Integer>,
                DynamicProblem<PermutationSolution<Integer>, ?>,
                Observable<AlgorithmData>>(crossover, mutation, observable)
                .setSelectionOperator(selection)
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
      default:
        algorithm = null;
    }

    application.setStreamingRuntime(new DefaultRuntime<MultiobjectiveTSPUpdateData, DefaultStreamingDataSource<MultiobjectiveTSPUpdateData,?>>())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(new DefaultStreamingDataSource())
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer2())
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory"))
            //.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirector2"))
            //.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirector3"))
            .run();
  }
}
