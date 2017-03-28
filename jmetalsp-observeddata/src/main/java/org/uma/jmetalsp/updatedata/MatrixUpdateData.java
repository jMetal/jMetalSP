package org.uma.jmetalsp.updatedata;

import org.uma.jmetalsp.UpdateData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface MatrixUpdateData<T> extends UpdateData {
	int getX() ;
	int getY() ;
	T getValue() ;
	Object getMatrixIdentifier() ;
}
