package org.uma.jmetalsp.observer;

import org.uma.jmetalsp.ObservedData;

import java.util.Collection;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observable<O extends ObservedData<?>> {
	void register(Observer<O> observer) ;
	void unregister(Observer<O> observer) ;

	void notifyObservers(O data);
	int numberOfRegisteredObservers() ;
	Collection<Observer<O>> getObservers() ;

	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;
}
