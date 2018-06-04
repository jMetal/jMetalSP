package org.uma.jmetalsp;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.io.Serializable;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicProblem<S extends Solution<?>, D extends ObservedData<?>>
        extends Problem<S>, Observer<D> ,Serializable {

  boolean hasTheProblemBeenModified() ;
	void reset() ;
}
