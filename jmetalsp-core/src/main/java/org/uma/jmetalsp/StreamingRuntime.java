package org.uma.jmetalsp;

import java.util.List;

/**
 * Interface representing the streaming runtime system.
 * @author Antonio J. Nebro <ajnebro@uma.es>
 */
public interface StreamingRuntime<D extends ObservedData, S extends StreamingDataSource<?,?>> {
    void startStreamingDataSources(List<S> streamingDataSourceList);
}
