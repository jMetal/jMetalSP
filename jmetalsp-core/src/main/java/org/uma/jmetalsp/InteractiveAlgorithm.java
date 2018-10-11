package org.uma.jmetalsp;

import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;

import java.util.List;

public interface InteractiveAlgorithm<S,R>  extends org.uma.jmetal.algorithm.InteractiveAlgorithm<S,R> {

  void restart(RestartStrategy restartStrategy);
  List<S> getPopulation();
  void compute();
  List<S> initializePopulation();
  void evaluate(List<S> population);
}
