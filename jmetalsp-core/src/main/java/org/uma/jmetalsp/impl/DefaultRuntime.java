package org.uma.jmetalsp.impl;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.StreamingRuntime;

import java.util.List;

/**
 * Default, thread-based, implementation of the {@link StreamingRuntime} interface
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultRuntime implements StreamingRuntime {

	@Override
	public void startStreamingDataSources(List<StreamingDataSource<?>> streamingDataSourceList) {
    for (StreamingDataSource<?> streamingDataSource : streamingDataSourceList) {
      Thread thread = new Thread(streamingDataSource);
      thread.start();
    }
  }
}
