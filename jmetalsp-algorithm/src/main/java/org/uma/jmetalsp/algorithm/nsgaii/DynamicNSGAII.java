//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalsp.algorithm.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.DynamicUpdate;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.KafkaBasedConsumer;
import org.uma.jmetalsp.observer.impl.KafkaObservable;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.qualityindicator.CoverageFront;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNRandomSolutions;

import java.util.*;

/**
 * Class implementing a dynamic version of NSGA-II. Most of the code of the original NSGA-II is
 * reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @todo Explain the behaviour of the dynamic algorithm
 */
public class DynamicNSGAII<S extends Solution<?>>
        extends NSGAII<S>
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData> {

    private int completedIterations;
    private boolean stopAtTheEndOfTheCurrentIteration = false;
    private RestartStrategy<S> restartStrategyForProblemChange;
    private Comparator<S> dominanceComparator;
    private Observable<AlgorithmObservedData> observable;
    private List<S> lastReceivedFront;
    private boolean autoUpdate;
    private CoverageFront<PointSolution> coverageFront;

    public DynamicNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize, int matingPoolSize,
                         int offspringPopulationSize, Comparator<S> dominanceComparator,
                         CrossoverOperator<S> crossoverOperator,
                         MutationOperator<S> mutationOperator,
                         SelectionOperator<List<S>, S> selectionOperator,
                         SolutionListEvaluator<S> evaluator,
                         Observable<AlgorithmObservedData> observable, boolean autoUpdate, CoverageFront<PointSolution> coverageFront) {

        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize,
                crossoverOperator,
                mutationOperator, selectionOperator, dominanceComparator, evaluator);

        this.completedIterations = 0;
        this.autoUpdate = autoUpdate;
        this.observable = observable;
        this.coverageFront = coverageFront;
        this.restartStrategyForProblemChange = new RestartStrategy<>(
                new RemoveFirstNSolutions<S>(populationSize),
                new CreateNRandomSolutions<S>());
    }


    public DynamicNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize,
                         CrossoverOperator<S> crossoverOperator,
                         MutationOperator<S> mutationOperator,
                         SelectionOperator<List<S>, S> selectionOperator,
                         SolutionListEvaluator<S> evaluator,
                         Comparator<S> dominanceComparator,
                         Observable<AlgorithmObservedData> observable, boolean autoUpdate, CoverageFront<PointSolution> coverageFront) {
        super(problem, maxEvaluations, populationSize, populationSize, populationSize, crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator);

        this.completedIterations = 0;
        this.observable = observable;
        this.autoUpdate = autoUpdate;
        this.coverageFront = coverageFront;
        this.restartStrategyForProblemChange = new RestartStrategy<>(
                new RemoveFirstNSolutions<S>(populationSize),
                new CreateNRandomSolutions<S>());
    }

    /**
     * main() method to run the algorithm as a process
     *
     * @param args
     */
    public static void main(String[] args) {
        String topicName = "prueba-solutionlist-topic-from-main";

        DynamicProblem<DoubleSolution, ObservedValue<Integer>> problem = new FDA2();

        // STEP 2. Create the algorithm
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
        MutationOperator<DoubleSolution> mutation =
                new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>();
        ;
        InvertedGenerationalDistance<PointSolution> igd =
                new InvertedGenerationalDistance<>();
        CoverageFront<PointSolution> coverageFront = new CoverageFront<>(0.005, igd);
        DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData> algorithm =
                new DynamicNSGAIIBuilder<>(crossover, mutation, new KafkaObservable<>(topicName, new AlgorithmObservedData()), coverageFront)
                        .setMaxEvaluations(50000)
                        .setPopulationSize(100)
                        .build(problem);

        algorithm.setRestartStrategy(new RestartStrategy<>(
                //new RemoveFirstNSolutions<>(50),
                //new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
                //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
                new RemoveNRandomSolutions(15),
                new CreateNRandomSolutions<DoubleSolution>()));

        //KafkaBasedConsumer<ObservedValue<Integer>> problemKafkaBasedConsumer =
        //        new KafkaBasedConsumer<>("prueba-int-topic-from-main", problem, new ObservedValue<>()) ;

        KafkaBasedConsumer<ObservedValue<Integer>> problemKafkaBasedConsumer =
                new KafkaBasedConsumer<>("prueba-tsp-topic-from-main", problem, new ObservedValue<>(), "avsc/TSPMatrixData.avsc");
        problemKafkaBasedConsumer.start();

        algorithm.run();
    }

    @Override
    public DynamicProblem<S, ?> getDynamicProblem() {
        return (DynamicProblem<S, ?>) super.getProblem();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        if (evaluations >= maxEvaluations) {

     /* double coverageValue=1.0;
      if (lastReceivedFront != null){
        Front referenceFront = new ArrayFront(lastReceivedFront);

        InvertedGenerationalDistance<PointSolution> igd =
                new InvertedGenerationalDistance<PointSolution>(referenceFront);
        List<S> list = getPopulation();
        List<PointSolution> pointSolutionList = new ArrayList<>();
        for (S s:list){
          PointSolution pointSolution = new PointSolution(s);
          pointSolutionList.add(pointSolution);
        }
        coverageValue = igd.evaluate(pointSolutionList);
      }



      if (coverageValue>1.5) {

        observable.setChanged();
        Map<String, Object> algorithmData = new HashMap<>();

        algorithmData.put("numberOfIterations", completedIterations);
        algorithmData.put("algorithmName", getName());
        algorithmData.put("problemName", problem.getName());
        algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives());

        observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) getPopulation(), algorithmData));
      }*/
            boolean coverage = false;
            if (lastReceivedFront != null) {
                Front referenceFront = new ArrayFront(lastReceivedFront);
                coverageFront.updateFront(referenceFront);
                List<PointSolution> pointSolutionList = new ArrayList<>();
                List<S> list = getPopulation();
                for (S s : list) {
                    PointSolution pointSolution = new PointSolution(s);
                    pointSolutionList.add(pointSolution);
                }
                coverage = coverageFront.isCoverageWithLast(pointSolutionList);

            }

            if (getDynamicProblem() instanceof DynamicUpdate && autoUpdate) {
                ((DynamicUpdate) getDynamicProblem()).update();
            }

            if (coverage) {
                observable.setChanged();
                Map<String, Object> algorithmData = new HashMap<>();
                algorithmData.put("numberOfIterations", completedIterations);
                algorithmData.put("algorithmName", getName());
                algorithmData.put("problemName", problem.getName());
                algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives());


                observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) getPopulation(), algorithmData));
            }
            lastReceivedFront = getPopulation();
            restart();
            evaluator.evaluate(getPopulation(), getDynamicProblem());

            initProgress();
            completedIterations++;
        }
        return stopAtTheEndOfTheCurrentIteration;
    }

    @Override
    protected void updateProgress() {
        if (getDynamicProblem().hasTheProblemBeenModified()) {
            restart();

            evaluator.evaluate(getPopulation(), getDynamicProblem());
            getDynamicProblem().reset();
        }
        evaluations += getMaxPopulationSize();
    }

    @Override
    public String getName() {
        return "DynamicNSGAII";
    }

    @Override
    public String getDescription() {
        return "Dynamic version of algorithm NSGA-II";
    }

    @Override
    public Observable<AlgorithmObservedData> getObservable() {
        return this.observable;
    }

    @Override
    public void restart() {
        this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
    }

    @Override
    public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
        this.restartStrategyForProblemChange = (RestartStrategy<S>) restartStrategy;
    }
}
