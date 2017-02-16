package org.uma.jmetalsp.examples.dynamicnsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCellBuilder;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.algorithm.smpso.DynamicSMPSOBuilder;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.consumer.SimpleSolutionListConsumer2;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.streamingruntime.impl.DefaultRuntime;
import org.uma.jmetalsp.updatedata.AlgorithmData;
import org.uma.jmetalsp.util.Observable;
import org.uma.jmetalsp.util.impl.DefaultObservable;

import java.io.IOException;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousApplication {

  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            FDAUpdateData,
            DynamicProblem<DoubleSolution, FDAUpdateData>,
            DynamicAlgorithm<List<DoubleSolution>,FDAUpdateData>,
            StreamingFDAUpdateData> application;
    application = new JMetalSPApplication<>();

	  // Problem configuration
    Observable<FDAUpdateData> fdaUpdateDataObservable = new DefaultObservable<>("timeData") ;
	  DynamicProblem<DoubleSolution, FDAUpdateData> problem = new FDA2(fdaUpdateDataObservable);

	  // Algorithm configuration
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);

    String defaultAlgorithm = "NSGAII";

    DynamicAlgorithm<List<DoubleSolution>,AlgorithmData> algorithm;
    Observable<AlgorithmData> observable = new DefaultObservable<>("NSGAII") ;

    switch (defaultAlgorithm) {
      case "NSGAII":
        algorithm = new DynamicNSGAIIBuilder<
                DoubleSolution,
                DynamicProblem<DoubleSolution, ?>,
                Observable<AlgorithmData>>(crossover, mutation, observable)
                .setMaxEvaluations(50000)
                .setPopulationSize(100)
                .build(problem);
        break;

      case "MOCell":
        algorithm = new DynamicMOCellBuilder<>(crossover, mutation, observable)
                .setMaxEvaluations(500000)
                .setPopulationSize(100)
                .build(problem);
        break;

      case "SMPSO":
        algorithm = new DynamicSMPSOBuilder<>(
                mutation, new CrowdingDistanceArchive<>(100), observable)
                .setMaxIterations(500)
                .setSwarmSize(100)
                .build(problem);
        break;

      default:
        algorithm = null;
    }

    application.setStreamingRuntime(new DefaultRuntime<FDAUpdateData, StreamingFDAUpdateData>())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(new StreamingFDAUpdateData(fdaUpdateDataObservable))
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer2())
            //.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory"))
            //.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirector2"))
            //.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirector3"))
            .run();
  }
}
