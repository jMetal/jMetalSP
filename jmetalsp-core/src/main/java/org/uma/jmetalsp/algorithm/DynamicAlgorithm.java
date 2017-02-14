package org.uma.jmetalsp.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.measure.Measurable;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.khaos.perception.core.Observable;


/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicAlgorithm<Result, O extends Observable> extends Algorithm<Result> {
  DynamicProblem<?, ?> getDynamicProblem() ;
  int getCompletedIterations() ;
  void stopTheAlgorithm() ;
  void restart(int percentageOfSolutionsToRemove);
  O getObservable() ;
}
