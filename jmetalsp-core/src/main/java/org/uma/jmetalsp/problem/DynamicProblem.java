package org.uma.jmetalsp.problem;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicProblem<S extends Solution<?>, D extends UpdateData>
        extends Problem<S> {
  boolean hasTheProblemBeenModified() ;
  void reset() ;
  void update(D data) ;
}
