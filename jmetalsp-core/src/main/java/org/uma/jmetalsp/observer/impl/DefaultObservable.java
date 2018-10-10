package org.uma.jmetalsp.observer.impl;

import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultObservable<O extends ObservedData<?>> implements Observable<O> {
	private Set<Observer<O>> observers ;
	private boolean dataHasChanged ;

	public DefaultObservable() {
		observers = new HashSet<>() ;
		dataHasChanged = false ;
	}

	@Override
	public void register(Observer<O> observer) {
		observers.add(observer) ;
	}

	@Override
	public void unregister(Observer<O> observer) {
		observers.remove(observer) ;
	}

	@Override
	public void notifyObservers(O data) {
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
	public Collection<Observer<O>> getObservers() {
		return observers ;
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
