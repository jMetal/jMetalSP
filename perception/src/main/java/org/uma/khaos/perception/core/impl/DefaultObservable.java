package org.uma.khaos.perception.core.impl;

import org.uma.khaos.perception.core.Observable;
import org.uma.khaos.perception.core.ObservableData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultObservable implements Observable {
	private Map<String, ObservableData<?>> observables ;

	public DefaultObservable() {
		observables = new HashMap<>() ;
	}

	@Override
	public void setObservableData(String name, ObservableData<?> observableData) {
		observables.put(name, observableData) ;
	}

	@Override
	public ObservableData<?> getObservableData(String name) {
		return observables.get(name);
	}
}
