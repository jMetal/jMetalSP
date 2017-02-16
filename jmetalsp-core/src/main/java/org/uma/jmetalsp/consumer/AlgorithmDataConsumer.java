package org.uma.jmetalsp.consumer;

import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.updatedata.UpdateData;
import org.uma.jmetalsp.util.Observer;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface AlgorithmDataConsumer<D extends UpdateData> extends Runnable, Observer {
  void setAlgorithm(DynamicAlgorithm<?,D> algorithm) ;
  DynamicAlgorithm<?,D> getAlgorithm() ;
  Observer getObserver() ;
}
