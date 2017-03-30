package org.uma.jmetalsp.impl;

import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.StreamingRuntime;
import org.uma.jmetalsp.perception.Observable;

import java.util.List;

/**
 * Created by khaosdev on 2/9/17.
 */
public class DefaultRuntime<
        D extends ObservedData,
        O extends Observable<D>,
        S extends StreamingDataSource<D,O>> implements StreamingRuntime<D, O, S> {
	@Override
	public void startStreamingDataSources(List<S> streamingDataSourceList) {
    System.out.println("Default runtime") ;

    for (StreamingDataSource<?, ?> streamingDataSource : streamingDataSourceList) {
      Thread thread = new Thread(streamingDataSource);
      thread.start();
    }
    System.out.println("Started streaming data sources") ;
  }
}
