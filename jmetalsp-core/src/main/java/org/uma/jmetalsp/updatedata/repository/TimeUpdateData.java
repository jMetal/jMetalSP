package org.uma.jmetalsp.updatedata.repository;

import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface TimeUpdateData extends UpdateData {
	double getTime() ;
	double getTimeInterval() ;
}
