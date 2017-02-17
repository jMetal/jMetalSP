package org.uma.jmetalsp.perception;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Observer {
	void update(Observable<?> observable, Object data) ;
}
