package org.uma.jmetalsp.updatedata;

import org.uma.jmetalsp.ObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface MatrixUpdateData<T> extends ObservedData {
	int getX() ;
	int getY() ;
	T getValue() ;
	Object getMatrixIdentifier() ;
}
