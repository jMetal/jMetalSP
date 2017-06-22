package org.uma.jmetalsp;

import org.uma.jmetalsp.observer.Observable;

import java.util.List;

/**
 * Interface representing the streaming runtime system.
 * @author Antonio J. Nebro <ajnebro@uma.es>
 */
public interface StreamingRuntime {
    void startStreamingDataSources(List<StreamingDataSource<?>> streamingDataSourceList);
}
