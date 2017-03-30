package org.uma.jmetalsp.observeddata;

import org.uma.jmetalsp.ObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MatrixObservedData<T> implements ObservedData {
	private int x ;
	private int y ;
	private T value ;
	Object matrixIdentifier ;

	public MatrixObservedData(Object id, int x, int y, T value) {
		this.matrixIdentifier = id ;
		this.x = x ;
		this.y = y ;
		this.value = value ;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public T getValue() {
		return value;
	}

	public Object getMatrixIdentifier() {
		return matrixIdentifier;
	}
}
