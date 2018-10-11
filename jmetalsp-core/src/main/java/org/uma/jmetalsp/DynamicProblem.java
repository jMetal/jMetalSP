package org.uma.jmetalsp;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.observer.Observer;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicProblem<S extends Solution<?>, O extends ObservedData<?>>
        extends Problem<S>, Observer<O> {

  boolean hasTheProblemBeenModified() ;
	void reset() ;
}
