package org.uma.jmetalsp.streamingruntime.impl;

import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.streamingruntime.StreamingRuntime;
import org.uma.jmetalsp.updatedata.UpdateData;

import java.util.List;

/**
 * Created by khaosdev on 2/9/17.
 */
public class DefaultRuntime<D extends UpdateData, S extends StreamingDataSource<D,?>> implements StreamingRuntime<D, S> {
	@Override
	public void startStreamingDataSources(List<S> streamingDataSourceList) {
		streamingDataSourceList.parallelStream()
						.forEach(s -> s.start());
	}
}
