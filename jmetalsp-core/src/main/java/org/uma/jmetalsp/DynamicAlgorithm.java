package org.uma.jmetalsp;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetalsp.observer.Observable;


/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicAlgorithm<Result, T extends ObservedData<?>> extends Algorithm<Result>{
  DynamicProblem<?, ?> getDynamicProblem() ;

  void restart();
  Observable<T> getObservable() ;
}
