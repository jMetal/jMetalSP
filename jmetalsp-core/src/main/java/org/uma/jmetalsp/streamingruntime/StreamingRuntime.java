package org.uma.jmetalsp.streamingruntime;

import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.UpdateData;

import java.util.List;

/**
 * Interface representing the streaming runtime system.
 * @author Antonio J. Nebro <ajnebro@uma.es>
 */
public interface StreamingRuntime<D extends UpdateData, S extends StreamingDataSource<D>> {
    void start() ;

    void startStreamingDataSources(List<S> streamingDataSourceList);
}
