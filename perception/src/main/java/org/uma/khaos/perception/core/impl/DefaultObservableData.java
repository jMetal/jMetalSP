package org.uma.khaos.perception.core.impl;

import org.uma.khaos.perception.core.ObservableData;
import org.uma.khaos.perception.core.Observer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultObservableData<Data> implements ObservableData<Data> {
	private Set<Observer<Data>> observers ;
	private boolean dataHasChanged ;

	public DefaultObservableData(){
		observers = new HashSet<>() ;
		dataHasChanged = false ;
	}

	@Override
	public void register(Observer<Data> observer) {
		observers.add(observer) ;
	}

	@Override
	public void unregister(Observer<Data> observer) {
		observers.remove(observer) ;
	}

	@Override
	public void notifyObservers(Data data) {
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
