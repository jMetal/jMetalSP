package org.uma.jmetalsp.consumer;

import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.khaos.perception.core.Observer;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface AlgorithmDataConsumer extends Runnable, Observer {
  void setAlgorithm(DynamicAlgorithm<?,?> algorithm) ;
  DynamicAlgorithm<?,?> getAlgorithm() ;
}
