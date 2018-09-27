package org.uma.jmetalsp.examples.streamingdatasource;

import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;

public class StreamingTSPDataSource
        implements StreamingDataSource<ObservedValue<TSPMatrixData>> {
    @Override
    public void run() {

    }

    @Override
    public Observable<ObservedValue<TSPMatrixData>> getObservable() {
        return null;
    }
}
