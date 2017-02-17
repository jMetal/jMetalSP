package org.uma.jmetalsp.updatedata;

import org.uma.jmetalsp.UpdateData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleMatrixUpdateData extends UpdateData {
	int getMatrixID() ;
	int getX() ;
	int getY() ;
	double getValue() ;
}
