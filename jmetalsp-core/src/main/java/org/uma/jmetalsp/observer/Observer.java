package org.uma.jmetalsp.observer;

import org.uma.jmetalsp.ObservedData;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer<D extends ObservedData<?>>  extends Serializable {
	void update(Observable<D> observable, D data) ;
}
