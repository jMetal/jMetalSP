package org.uma.khaos.perception.core2;

import java.util.Collection;
import java.util.List;

/**
 * Created by ajnebro on 14/2/17.
 */
public interface Observable {
  Collection<ObservableItem<?>> getObservableItemList() ;
}
