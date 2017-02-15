package org.uma.khaos.perception.core;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer {
	void update(Observable<?> observable, Object data) ;
}
