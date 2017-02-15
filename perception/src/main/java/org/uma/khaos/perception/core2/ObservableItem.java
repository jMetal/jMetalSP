package org.uma.khaos.perception.core2;

/**
 * Created by ajnebro on 14/2/17.
 */
public interface ObservableItem<V> {
  void register(ObservableItemListener<V> listener) ;
  void unregister(ObservableItemListener<V> observer) ;

  void notifyObservers(ObservableItem<V> observableItem, V data);
  int numberOfRegisteredObservers() ;
  void setChanged() ;
  boolean hasChanged() ;
  void clearChanged() ;
}
