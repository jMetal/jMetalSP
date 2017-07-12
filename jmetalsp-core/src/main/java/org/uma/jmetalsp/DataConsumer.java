package org.uma.jmetalsp;

import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface DataConsumer<D extends ObservedData<?>> extends Runnable, Observer<D> {
  @Override
  void run() ;
}
