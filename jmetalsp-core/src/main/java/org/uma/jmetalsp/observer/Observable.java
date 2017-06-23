package org.uma.jmetalsp.observer;

import org.uma.jmetalsp.ObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observable<D extends ObservedData<?>> {
	void register(Observer<D> observer) ;
	void unregister(Observer<D> observer) ;

	void notifyObservers(D data);
	int numberOfRegisteredObservers() ;
	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;
}
