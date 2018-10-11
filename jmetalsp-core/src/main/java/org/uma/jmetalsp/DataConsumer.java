package org.uma.jmetalsp;

import org.uma.jmetalsp.observer.Observer;

/**
 * Created by ajnebro on 21/4/16.
 */
public interface DataConsumer<O extends ObservedData<?>> extends Runnable, Observer<O> {
  @Override
  void run() ;
}
