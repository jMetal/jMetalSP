package org.uma.jmetalsp.streamingdatasource.impl;

import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.UpdateData;
import org.uma.jmetalsp.util.Observable;

/**
 * Created by ajnebro on 16/2/17.
 */
public class DefaultStreamingDataSource<D extends UpdateData, O extends Observable<D>>
        implements StreamingDataSource<D, O> {
  @Override
  public void run() {
    // do nothing
  }
}
