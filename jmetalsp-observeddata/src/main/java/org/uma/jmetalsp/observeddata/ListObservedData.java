package org.uma.jmetalsp.observeddata;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetalsp.ObservedData;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ListObservedData<D> implements ObservedData {
	private List<D> list;

	public ListObservedData(List<D> list) {
		this.list = list ;
	}

	public List<D> getList() {
		return list;
	}
}
