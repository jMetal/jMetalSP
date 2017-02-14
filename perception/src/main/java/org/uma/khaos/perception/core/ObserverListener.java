package org.uma.khaos.perception.core;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ObserverListener<Data> {
	void update(ObservableData<?> observable, Data data) ;
}
