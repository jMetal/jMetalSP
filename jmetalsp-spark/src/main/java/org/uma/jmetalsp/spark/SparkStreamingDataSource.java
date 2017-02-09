package org.uma.jmetalsp.spark;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface SparkStreamingDataSource<D extends UpdateData> extends StreamingDataSource<D>{
	void setStreamingContext(JavaStreamingContext streamingContext) ;
}
