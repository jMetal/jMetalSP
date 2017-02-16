package org.uma.jmetalsp.util;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer {
	void update(Observable<?> observable, Object data) ;
}
