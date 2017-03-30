package org.uma.jmetalsp;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetalsp.perception.Observable;


/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicAlgorithm<Result, D extends ObservedData, O extends Observable<D>>
        extends Algorithm<Result>{
  DynamicProblem<?, ?> getDynamicProblem() ;
  int getCompletedIterations() ;
  void stopTheAlgorithm() ;
  void restart(int percentageOfSolutionsToRemove);
  O getObservable() ;
}
