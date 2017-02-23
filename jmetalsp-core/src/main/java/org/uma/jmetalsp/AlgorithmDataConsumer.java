package org.uma.jmetalsp;

import org.uma.jmetalsp.perception.Observer;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface AlgorithmDataConsumer<D extends UpdateData> extends Runnable, Observer {
  void setAlgorithm(DynamicAlgorithm<?,D> algorithm) ;
  DynamicAlgorithm<?,D> getAlgorithm() ;
}
