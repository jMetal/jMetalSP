package org.uma.jmetalsp.algorithm.smpso;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicSMPSORPBuilder<
				P extends DynamicProblem<DoubleSolution, ?>> {

	private double c1Max;
	private double c1Min;
	private double c2Max;
	private double c2Min;
	private double r1Max;
	private double r1Min;
	private double r2Max;
	private double r2Min;
	private double weightMax;
	private double weightMin;
	private double changeVelocity1;
	private double changeVelocity2;

	private int swarmSize;
	private int maxIterations;

	protected MutationOperator<DoubleSolution> mutationOperator;
  private Observable<AlgorithmObservedData> observable ;
	private List<ArchiveWithReferencePoint<DoubleSolution>> leaders;
    private List<List<Double>> referencePoints;
	private SolutionListEvaluator<DoubleSolution> evaluator;

	public DynamicSMPSORPBuilder(MutationOperator<DoubleSolution> mutationOperator,
								 List<ArchiveWithReferencePoint<DoubleSolution>> leaders,
								 List<List<Double>> referencePoints,
                                 Observable<AlgorithmObservedData> observable) {
		this.leaders = leaders;
		this.referencePoints = referencePoints;
		swarmSize = 100;
		maxIterations = 250;

		r1Max = 1.0;
		r1Min = 0.0;
		r2Max = 1.0;
		r2Min = 0.0;
		c1Max = 2.5;
		c1Min = 1.5;
		c2Max = 2.5;
		c2Min = 1.5;
		weightMax = 0.1;
		weightMin = 0.1;
		changeVelocity1 = -1;
		changeVelocity2 = -1;

		this.mutationOperator = mutationOperator ;
		evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
		this.observable = observable ;
	}

	public DynamicSMPSORPBuilder<P> setSwarmSize(int swarmSize) {
		this.swarmSize = swarmSize;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setMutation(MutationOperator<DoubleSolution> mutation) {
		mutationOperator = mutation;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setC1Max(double c1Max) {
		this.c1Max = c1Max;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setC1Min(double c1Min) {
		this.c1Min = c1Min;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setC2Max(double c2Max) {
		this.c2Max = c2Max;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setC2Min(double c2Min) {
		this.c2Min = c2Min;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setR1Max(double r1Max) {
		this.r1Max = r1Max;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setR1Min(double r1Min) {
		this.r1Min = r1Min;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setR2Max(double r2Max) {
		this.r2Max = r2Max;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setR2Min(double r2Min) {
		this.r2Min = r2Min;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setWeightMax(double weightMax) {
		this.weightMax = weightMax;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setWeightMin(double weightMin) {
		this.weightMin = weightMin;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setChangeVelocity1(double changeVelocity1) {
		this.changeVelocity1 = changeVelocity1;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setChangeVelocity2(double changeVelocity2) {
		this.changeVelocity2 = changeVelocity2;

		return this;
	}

	public DynamicSMPSORPBuilder<P> setRandomGenerator(PseudoRandomGenerator randomGenerator) {
		JMetalRandom.getInstance().setRandomGenerator(randomGenerator);

		return this;
	}

	public DynamicSMPSORPBuilder<P> setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
		this.evaluator = evaluator ;

		return this ;
	}

	public DynamicSMPSORP build(P problem) {
		return new DynamicSMPSORP(problem,  swarmSize,  leaders, referencePoints, mutationOperator,
				maxIterations, r1Min, r1Max,r2Min, r2Max, c1Min, c1Max, c2Min, c2Max,weightMin,
		weightMax, changeVelocity1, changeVelocity2,  evaluator,
				 observable);
	}
}