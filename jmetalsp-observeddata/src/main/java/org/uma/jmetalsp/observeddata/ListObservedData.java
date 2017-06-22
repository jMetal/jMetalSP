package org.uma.jmetalsp.observeddata;

import org.uma.jmetalsp.ObservedData;

import java.util.ArrayList;
import java.util.List;

/**
 * Observed data including a list of values that is copied instead or merely being returned to observers
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ListObservedData<D> implements ObservedData<List<D>> {
	private List<D> list ;

	public ListObservedData(List<D> list) {
		this.list = new ArrayList<>() ;
		for (D element: list) {
			this.list.add(element) ;
		}
	}

	public List<D> getData() {
		return list;
	}
}
