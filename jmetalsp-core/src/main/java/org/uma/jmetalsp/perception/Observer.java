package org.uma.jmetalsp.perception;

import org.uma.jmetalsp.ObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer<D extends ObservedData> {
	void update(Observable<D> observable, D data) ;
}
