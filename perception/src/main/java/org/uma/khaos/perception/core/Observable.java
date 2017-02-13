package org.uma.khaos.perception.core;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observable {
	void setObservableData(String name, ObservableData<?> observableData) ;
	ObservableData<?> getObservableData(String name) ;
}
