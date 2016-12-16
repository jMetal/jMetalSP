package org.uma.jmetalsp.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.measure.Measurable;
import org.uma.jmetalsp.problem.DynamicProblem;


/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicAlgorithm<Result> extends Algorithm<Result>, Measurable {
  DynamicProblem<?, ?> getDynamicProblem() ;
  int getCompletedIterations() ;
  void stopTheAlgorithm() ;
}
