package org.uma.jmetalsp.algorithm.nsgaiii;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Class implementing a dynamic version of NSGA-III. Most of the code of the original NSGA-III is
 * reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @todo Explain the behaviour of the dynamic algorithm
 *
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */

public class DynamicNSGAIII  <S extends Solution<?>> extends NSGAIII<S> implements
        DynamicAlgorithm<List<S>, AlgorithmObservedData<S>> {
    private int completedIterations ;
    private boolean stopAtTheEndOfTheCurrentIteration = false ;
    private RestartStrategy<S> restartStrategyForProblemChange ;

    Observable<AlgorithmObservedData<S>> observable ;
    public DynamicNSGAIII(DynamicNSGAIIIBuilder builder,Observable<AlgorithmObservedData<S>> observable) {
        super(builder);
        this.observable=observable;

    }

    @Override
    public DynamicProblem<?, ?> getDynamicProblem() {
        return (DynamicProblem<S, ?>) super.getProblem();
    }
    @Override
    protected boolean isStoppingConditionReached() {
        if (iterations >= maxIterations) {
            observable.setChanged() ;

            Map<String, Object> algorithmData = new HashMap<>() ;

            algorithmData.put("numberOfIterations",completedIterations);
            observable.notifyObservers(new AlgorithmObservedData<S>(getPopulation(), algorithmData));

            restart();
            evaluator.evaluate(getPopulation(), (Problem<S>) getDynamicProblem()) ;

            initProgress();
            completedIterations++;
        }
        return stopAtTheEndOfTheCurrentIteration ;
    }
    @Override protected void updateProgress() {
        if (getDynamicProblem().hasTheProblemBeenModified()) {
            restart();

            evaluator.evaluate(getPopulation(), (Problem<S>) getDynamicProblem()) ;
            getDynamicProblem().reset();
        }
        iterations += getMaxPopulationSize() ;
    }

    @Override
    public String getName() {
        return "DynamicNSGAIII";
    }

    @Override
    public String getDescription() {
        return "Dynamic version of algorithm NSGA-III";
    }

    @Override
    public Observable<AlgorithmObservedData<S>> getObservable() {
        return this.observable ;
    }

    @Override
    public void restart() {
        this.restartStrategyForProblemChange.restart(getPopulation(), (DynamicProblem<S, ?>)getProblem());
    }

    @Override
    public void setRestartStrategy(RestartStrategy<?> restartStrategy) {
        this.restartStrategyForProblemChange = (RestartStrategy<S>) restartStrategy;
    }
}
