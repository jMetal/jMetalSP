package org.uma.jmetalsp.observer.impl;

import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultObservable<D extends ObservedData<?>> implements Observable<D> {
	private Set<Observer<D>> observers ;
	private boolean dataHasChanged ;

	public DefaultObservable() {
		observers = new HashSet<>() ;
		dataHasChanged = false ;
	}

	@Override
	public void register(Observer<D> observer) {
		observers.add(observer) ;
	}

	@Override
	public void unregister(Observer<D> observer) {
		observers.remove(observer) ;
	}

	@Override
	public void notifyObservers(D data) {
		if (dataHasChanged) {
			observers.stream().forEach(observer -> observer.update(this, data));
		}
		clearChanged();
	}

	@Override
	public int numberOfRegisteredObservers() {
		return observers.size();
	}

	@Override
	public void setChanged() {
		dataHasChanged = true ;
	}

	@Override
	public boolean hasChanged() {
		return dataHasChanged ;
	}

	@Override
	public void clearChanged() {
		dataHasChanged = false ;
	}
}
