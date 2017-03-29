package org.uma.jmetalsp.updatedata.impl;

import org.uma.jmetalsp.updatedata.MatrixObservedData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultMatrixUpdateData<T> implements MatrixObservedData<T> {
  private int x ;
  private int y ;
  private T value ;
  Object matrixIdentifier ;

  public DefaultMatrixUpdateData(Object id, int x, int y, T value) {
    this.matrixIdentifier = id ;
    this.x = x ;
    this.y = y ;
    this.value = value ;
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public T getValue() {
    return value;
  }

  @Override
  public Object getMatrixIdentifier() {
    return matrixIdentifier;
  }

}
