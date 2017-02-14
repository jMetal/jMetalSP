package org.uma.khaos.perception.core;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ObservableData<Data> {
	void register(ObserverListener<Data> observer) ;
	void unregister(ObserverListener<Data> observer) ;

	void notifyObservers(Data data);
	int numberOfRegisteredObservers() ;
	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;


}
