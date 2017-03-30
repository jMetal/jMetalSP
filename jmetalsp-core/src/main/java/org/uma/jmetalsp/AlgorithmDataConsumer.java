package org.uma.jmetalsp;

import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface AlgorithmDataConsumer<
        D extends ObservedData,
        A extends DynamicAlgorithm<?, D, ? extends Observable<D>>> extends Runnable, Observer<D> {

  A getAlgorithm() ;
  @Override void run() ;
}
