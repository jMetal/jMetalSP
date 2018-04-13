package org.uma.jmetalsp;

import java.util.List;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;

public interface InteractiveAlgorithm<S,R>  extends org.uma.jmetal.algorithm.InteractiveAlgorithm<S,R> {

  public void restart(RestartStrategy restartStrategy);
  public List<S> getPopulation();
  public void compute();//List<S> population
  public List<S> initializePopulation();
  public void evaluate(List<S> population);
}
