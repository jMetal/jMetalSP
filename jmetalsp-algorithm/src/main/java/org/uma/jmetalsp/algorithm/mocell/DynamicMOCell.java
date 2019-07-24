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

package org.uma.jmetalsp.algorithm.mocell;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.solutionattribute.impl.LocationAttribute;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.DynamicUpdate;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.qualityindicator.CoverageFront;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class implementing a dynamic version of MOCell. Most of the code of the original MOCell algorithm
 * is reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @todo Explain the behaviour of the dynamic algorithm
 */
public class DynamicMOCell<S extends Solution<?>>
        extends MOCell<S>
        implements DynamicAlgorithm<List<S>, AlgorithmObservedData> {

    Observable<AlgorithmObservedData> observable;
    private int completedIterations;
    private boolean stopAtTheEndOfTheCurrentIteration = false;
    private RestartStrategy<S> restartStrategyForProblemChange;
    private List<S> lastReceivedFront;
    private boolean autoUpdate;
    private CoverageFront<PointSolution> coverageFront;

    public DynamicMOCell(DynamicProblem<S, ?> problem,
                         int maxEvaluations,
                         int populationSize,
                         BoundedArchive<S> archive,
                         Neighborhood<S> neighborhood,
                         CrossoverOperator<S> crossoverOperator,
                         MutationOperator<S> mutationOperator,
                         SelectionOperator<List<S>, S> selectionOperator,
                         SolutionListEvaluator<S> evaluator,
                         Observable<AlgorithmObservedData> observable, boolean autoUpdate, CoverageFront<PointSolution> coverageFront) {
        super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator,
                selectionOperator, evaluator);

        this.completedIterations = 0;
        this.observable = observable;
        this.autoUpdate = autoUpdate;
        this.coverageFront = coverageFront;
        this.restartStrategyForProblemChange = new RestartStrategy<>(
                new RemoveFirstNSolutions<S>(populationSize),
                new CreateNRandomSolutions<S>());
    }

    @Override
    public DynamicProblem<S, ?> getDynamicProblem() {
        return (DynamicProblem<S, ?>) super.getProblem();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        if (evaluations >= maxEvaluations) {
     /* observable.setChanged() ;
      Map<String, Object> algorithmData = new HashMap<>() ;

      algorithmData.put("numberOfIterations",completedIterations);
      algorithmData.put("algorithmName", getName()) ;
      algorithmData.put("problemName", problem.getName()) ;
      algorithmData.put("numberOfObjectives", problem.getNumberOfObjectives()) ;

      observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) getResult(), algorithmData));*/

            boolean coverage = false;
            if (lastReceivedFront != null) {
                Front referenceFront = new ArrayFront(lastReceivedFront);
                coverageFront.updateFront(referenceFront);
                List<PointSolution> pointSolutionList = new ArrayList<>();
                List<S> list = getResult();
                for (S s : list) {
                    PointSolution pointSolution = new PointSolution(s);
                    pointSolutionList.add(pointSolution);
                }
                coverage = coverageFront.isCoverage(pointSolutionList);

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
                observable.notifyObservers(new AlgorithmObservedData((List<Solution<?>>) getResult(), algorithmData));
            }
            lastReceivedFront = getResult();
            restart();
            completedIterations++;
        }
        return stopAtTheEndOfTheCurrentIteration;
    }

    @Override
    public void restart() {
        this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
        SolutionListUtils.removeSolutionsFromList(getResult(), getResult().size());//clean archive
        location = new LocationAttribute<>(getPopulation());
        evaluator.evaluate(getPopulation(), getDynamicProblem());
        initProgress();
    }

    @Override
    protected void updateProgress() {
        if (getDynamicProblem().hasTheProblemBeenModified()) {
            restart();
            getDynamicProblem().reset();
        }
        evaluations++;
        currentIndividual = (currentIndividual + 1) % getMaxPopulationSize();
    }

    @Override
    public String getName() {
        return "DynamicMOCell";
    }

    @Override
    public String getDescription() {
        return "Dynamic version of algorithm MOCell";
    }

    @Override
    public Observable<AlgorithmObservedData> getObservable() {
        return this.observable;
    }

    @Override
    public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
        this.restartStrategyForProblemChange = (RestartStrategy<S>) restartStrategy;
    }
}
