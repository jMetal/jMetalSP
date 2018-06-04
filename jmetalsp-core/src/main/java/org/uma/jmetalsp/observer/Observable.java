package org.uma.jmetalsp.observer;

import org.uma.jmetalsp.ObservedData;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observable<D extends ObservedData<?>> extends Serializable {
	void register(Observer<D> observer) ;
	void unregister(Observer<D> observer) ;

	void notifyObservers(D data);
	int numberOfRegisteredObservers() ;
	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;
}
