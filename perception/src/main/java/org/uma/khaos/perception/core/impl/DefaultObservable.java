package org.uma.khaos.perception.core.impl;

import org.uma.khaos.perception.core.Observable;
import org.uma.khaos.perception.core.Observer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultObservable<Data> implements Observable<Data> {
	private Set<Observer> observers ;
	private boolean dataHasChanged ;
	private String name ;

	public DefaultObservable(String name){
		observers = new HashSet<>() ;
		dataHasChanged = false ;
		this.name = name ;
	}

	@Override
	public void register(Observer observer) {
		observers.add(observer) ;
	}

	@Override
	public void unregister(Observer observer) {
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

	@Override
  public String getName() {
	  return name ;
  }

}
