package org.uma.jmetalsp.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.updatedata.UpdateData;
import org.uma.jmetalsp.util.Observable;


/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicAlgorithm<Result, D extends UpdateData>
        extends Algorithm<Result>{
  DynamicProblem<?, ?> getDynamicProblem() ;
  int getCompletedIterations() ;
  void stopTheAlgorithm() ;
  void restart(int percentageOfSolutionsToRemove);
  Observable<D> getObservable() ;
}
