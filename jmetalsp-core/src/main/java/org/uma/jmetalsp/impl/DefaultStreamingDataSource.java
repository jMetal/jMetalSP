package org.uma.jmetalsp.impl;

import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.perception.Observable;

/**
 * Created by ajnebro on 16/2/17.
 */
public class DefaultStreamingDataSource<D extends ObservedData, O extends Observable<D>>
        implements StreamingDataSource<D, O> {
  @Override
  public void run() {
    // do nothing
  }
}
