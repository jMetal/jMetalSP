package org.uma.jmetalsp.consumer;

import org.uma.jmetalsp.algorithm.DynamicAlgorithm;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface AlgorithmDataConsumer extends Runnable {
  void setAlgorithm(DynamicAlgorithm<?> algorithm) ;
  DynamicAlgorithm<?> getAlgorithm() ;
}
