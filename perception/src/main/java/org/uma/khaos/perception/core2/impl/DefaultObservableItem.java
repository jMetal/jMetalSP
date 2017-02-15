package org.uma.khaos.perception.core2.impl;

import org.uma.khaos.perception.core2.ObservableItem;
import org.uma.khaos.perception.core2.ObservableItemListener;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultObservableItem<Data> implements ObservableItem<Data> {
	private Set<ObservableItemListener<Data>> listeners ;
	private boolean dataHasChanged ;

	public DefaultObservableItem(){
		listeners = new HashSet<>() ;
		dataHasChanged = false ;
	}

	@Override
	public void register(ObservableItemListener<Data> listener) {
		listeners.add(listener) ;
	}

	@Override
	public void unregister(ObservableItemListener<Data> listener) {
		listeners.remove(listener) ;
	}

	@Override
	public void notifyObservers(ObservableItem<Data> observableItem, Data data) {
		if (dataHasChanged) {
			listeners.stream().forEach(observer -> observer.update(this, data));
		}
		clearChanged();
	}

	@Override
	public int numberOfRegisteredObservers() {
		return listeners.size();
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
