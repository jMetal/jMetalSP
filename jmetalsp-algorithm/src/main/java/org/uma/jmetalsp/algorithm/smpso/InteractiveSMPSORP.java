package org.uma.jmetalsp.algorithm.smpso;

import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.InteractiveAlgorithm;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;


import java.util.ArrayList;
import java.util.List;

public class InteractiveSMPSORP extends SMPSORP implements InteractiveAlgorithm<DoubleSolution,List<DoubleSolution>> {
    private DynamicProblem<DoubleSolution, ?> dynamicProblem;
    /**
     * Constructor
     *
     * @param problem
     * @param swarmSize
     * @param leaders
     * @param referencePoints
     * @param mutationOperator
     * @param maxIterations
     * @param r1Min
     * @param r1Max
     * @param r2Min
     * @param r2Max
     * @param c1Min
     * @param c1Max
     * @param c2Min
     * @param c2Max
     * @param weightMin
     * @param weightMax
     * @param changeVelocity1
     * @param changeVelocity2
     * @param evaluator
     */
    public InteractiveSMPSORP(DynamicProblem<DoubleSolution, ?> problem, int swarmSize, List<ArchiveWithReferencePoint<DoubleSolution>> leaders, List<List<Double>> referencePoints, MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min, double r1Max, double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin, double weightMax, double changeVelocity1, double changeVelocity2, SolutionListEvaluator<DoubleSolution> evaluator) {
        super((DoubleProblem) problem, swarmSize, leaders, referencePoints, mutationOperator, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);
        dynamicProblem = problem;
    }

    public DynamicProblem<DoubleSolution, ?> getDynamicProblem() {
        return dynamicProblem;
    }

    @Override
    public void restart(RestartStrategy restartStrategy) {
        restartStrategy.restart(getSwarm(), getDynamicProblem());
        //SolutionListUtils.restart(getSwarm(), (DoubleProblem) getDynamicProblem(), 100);
        SolutionListUtils.removeSolutionsFromList(getResult(), getResult().size());
        evaluator.evaluate(getSwarm(), (DoubleProblem) getDynamicProblem());
        initializeVelocity(getSwarm());
        initializeParticlesMemory(getSwarm());
        initializeLeader(getSwarm());
        initProgress();
    }

    @Override
    public List<DoubleSolution> getPopulation() {
        return super.getSwarm();
    }

    @Override
    public void compute() {
        updateVelocity(super.getSwarm());
        updatePosition(super.getSwarm());
        perturbation(super.getSwarm());
        super.setSwarm(evaluateSwarm(super.getSwarm()));
        updateLeaders(super.getSwarm()) ;
        updateParticlesMemory(super.getSwarm()) ;
        updateProgress();

    }

    @Override
    public List<DoubleSolution> initializePopulation() {
        setSwarm(createInitialSwarm());
        return super.getSwarm();
    }

    @Override
    public void evaluate(List<DoubleSolution> population) {
        setSwarm(evaluator.evaluate(getSwarm(), getDynamicProblem()));

    }

    @Override
    public void updatePointOfInterest(List<Double> newReferencePoints) {
        List<List<Double>> referencePoints = new ArrayList<>();
        int numberOfPoints= newReferencePoints.size()/getDynamicProblem().getNumberOfObjectives();
        int i=0;
        while (i<newReferencePoints.size()){
            int j= numberOfPoints-1;
            List<Double> aux = new ArrayList<>();
            while(j>=0){
                aux.add(newReferencePoints.get(i));
                i++;
                j--;
            }
            referencePoints.add(aux);
        }
        changeReferencePoints(referencePoints);
    }
}
