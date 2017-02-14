package org.uma.khaos.perception.core;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ObservableData<Data> {
	void register(Observer<Data> observer) ;
	void unregister(Observer<Data> observer) ;

	void notifyObservers(Data data);
	int numberOfRegisteredObservers() ;
	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;


}
