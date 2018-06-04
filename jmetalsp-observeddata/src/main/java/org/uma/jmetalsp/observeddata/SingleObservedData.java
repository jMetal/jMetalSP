package org.uma.jmetalsp.observeddata;

import org.uma.jmetalsp.ObservedData;

import java.io.Serializable;

/**
 * Class implementing a the {@link ObservedData} interface.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SingleObservedData<D> implements ObservedData<D> ,Serializable {
	private D value ;

	public SingleObservedData(D value) {
		this.value = value ;
	}

	@Override
	public D getData() {
		return value;
	}
}
