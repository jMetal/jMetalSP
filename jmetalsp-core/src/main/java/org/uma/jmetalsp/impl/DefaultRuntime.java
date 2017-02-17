package org.uma.jmetalsp.impl;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.StreamingRuntime;
import org.uma.jmetalsp.UpdateData;

import java.util.List;

/**
 * Created by khaosdev on 2/9/17.
 */
public class DefaultRuntime<D extends UpdateData, S extends StreamingDataSource<D,?>> implements StreamingRuntime<D, S> {
	@Override
	public void startStreamingDataSources(List<S> streamingDataSourceList) {
    System.out.println("Default runtime") ;

    for (StreamingDataSource<?, ?> streamingDataSource : streamingDataSourceList) {
      Thread thread = new Thread(streamingDataSource);
      thread.start();
    }
    System.out.println("Started streaming data sources") ;
  }

    /*
    System.out.println("Default runtime") ;
    streamingDataSourceList.parallelStream()
						.forEach(s -> s.start());
    System.out.println("Started streaming data sources") ;
	}
*/
}
