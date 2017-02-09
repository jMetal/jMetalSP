package org.uma.jmetalsp.examples.dynamicnsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAII;
import org.uma.jmetalsp.algorithm.nsgaii.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.streamingruntime.impl.DefaultRuntime;

import java.io.IOException;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicContinuousApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		JMetalSPApplication<
						FDAUpdateData,
						DynamicProblem<DoubleSolution, FDAUpdateData>,
						DynamicAlgorithm<DoubleSolution>,
						StreamingFDAUpdateData> application;
		application = new JMetalSPApplication<>();

		DynamicProblem<DoubleSolution, FDAUpdateData> problem = new FDA2() ;
		CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0) ;
		MutationOperator<DoubleSolution> mutation = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0) ;

		DynamicNSGAII<DoubleSolution> algorithm ;
		algorithm = new DynamicNSGAIIBuilder<DoubleSolution, DynamicProblem<DoubleSolution, FDAUpdateData>>(crossover, mutation)
						.build(problem) ;

		/*
		DynamicMOCell<DoubleSolution> algorithm2 ;
		algorithm2 = new DynamicMOCellBuilder<DoubleSolution, DynamicProblem<DoubleSolution, FDAUpdateData>>(crossover, mutation)
						.build(problem) ;
		*/

		application.setStreamingRuntime(new DefaultRuntime<FDAUpdateData, StreamingFDAUpdateData>())
						.setProblem(problem)
						.setAlgorithm(algorithm)
						.addStreamingDataSource(new StreamingFDAUpdateData(problem))
						.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory"))
						.run();

	}
}
