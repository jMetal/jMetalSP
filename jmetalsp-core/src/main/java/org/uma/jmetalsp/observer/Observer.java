package org.uma.jmetalsp.observer;

import org.uma.jmetalsp.ObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer<O extends ObservedData<?>> {
	void update(Observable<O> observable, O data) ;
}
