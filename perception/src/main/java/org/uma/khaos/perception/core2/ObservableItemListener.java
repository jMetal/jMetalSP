package org.uma.khaos.perception.core2;

/**
 * Created by ajnebro on 14/2/17.
 */
public interface ObservableItemListener<V> {
  void update(ObservableItem<V> observableItem, V value) ;
}
