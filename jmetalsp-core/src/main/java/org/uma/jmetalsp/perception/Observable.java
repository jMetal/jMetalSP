package org.uma.jmetalsp.perception;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observable<Data> {
	void register(Observer observer) ;
	void unregister(Observer observer) ;

	void notifyObservers(Data data);
	int numberOfRegisteredObservers() ;
	void setChanged() ;
	boolean hasChanged() ;
	void clearChanged() ;
  String getName() ;

}
