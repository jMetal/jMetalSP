package org.uma.jmetalsp.examples.dynamicnsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAII;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.problem.fda.FDA;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.problem.fda.fda1.FDA1;
import org.uma.jmetalsp.problem.fda.fda1.FDA1ProblemBuilder;
import org.uma.jmetalsp.streamingruntime.impl.DefaultRuntime;

import java.io.IOException;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousNSGAIIApplication {


	public static void main(String[] args) throws IOException, InterruptedException {
		JMetalSPApplication<FDAUpdateData, FDA, DynamicNSGAII<DoubleSolution>, StreamingFDAUpdateData> application;
		application = new JMetalSPApplication<>();

		CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0) ;
		MutationOperator<DoubleSolution> mutation = new PolynomialMutation(1.0/20.0, 20.0) ;

		application.setStreamingRuntime(new DefaultRuntime<FDAUpdateData, StreamingFDAUpdateData>())
						.setProblemBuilder(new FDA1ProblemBuilder(20, 2))
						.setAlgorithmBuilder(new DynamicNSGAIIBuilder<DoubleSolution, FDA1>(crossover, mutation))
						.addStreamingDataSource(new StreamingFDAUpdateData())
						.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory"))
						.run();


	/*
	      application
					      .setStreamingRuntime(new DefautRuntime)
					.setProblemBuilder(problemBuilder)
              .setAlgorithmBuilder(new DynamicMOCellBuilder())
					.addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
					.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer(outputDirectoryName))
					.addStreamingDataSource(new StreamingKafkaFDA(streamingConfigurationFDA))
					.run();
					*/
	}
}
