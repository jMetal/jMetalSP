package org.uma.jmetalsp;

import org.uma.jmetalsp.observer.Observable;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface StreamingDataSource<D extends ObservedData<?>> extends Runnable {
	void run() ;
  Observable<D> getObservable() ;
}

