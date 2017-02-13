package org.uma.khaos.perception.core;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer<Data> {
	void update(ObservableData<Data> observable, Data data) ;
}
