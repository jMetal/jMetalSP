package org.uma.jmetalsp.spark;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.UpdateData;
import org.uma.khaos.perception.core.Observable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface SparkStreamingDataSource<D extends UpdateData, O extends Observable<D>> extends StreamingDataSource<D, O> {
	void setStreamingContext(JavaStreamingContext streamingContext) ;
}
