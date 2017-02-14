package org.uma.jmetalsp.consumer;

import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.khaos.perception.core.Observable;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface AlgorithmDataConsumer extends Runnable, Observable {
  void setAlgorithm(DynamicAlgorithm<?,?> algorithm) ;
  DynamicAlgorithm<?,?> getAlgorithm() ;
}
