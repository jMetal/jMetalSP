package org.uma.jmetalsp;

import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.io.Serializable;

/**
 * Class representing data observed by {@link Observer} entities and retrieved by {@link Observable} entities
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ObservedData<T> extends Serializable {
  String toJson() ;
  ObservedData fromJson(String jsonString) ;
  T getData();
}
