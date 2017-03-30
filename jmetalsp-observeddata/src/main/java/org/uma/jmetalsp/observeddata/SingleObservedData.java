package org.uma.jmetalsp.observeddata;

import org.uma.jmetalsp.ObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SingleObservedData<D> implements ObservedData {
	private D value ;

	public SingleObservedData(D value) {
		this.value = value ;
	}

	public D getValue() {
		return value;
	}
}
