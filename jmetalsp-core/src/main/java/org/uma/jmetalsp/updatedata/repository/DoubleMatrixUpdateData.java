package org.uma.jmetalsp.updatedata.repository;

import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleMatrixUpdateData extends UpdateData {
	int getMatrixID() ;
	int getX() ;
	int getY() ;
	double getValue() ;
}
