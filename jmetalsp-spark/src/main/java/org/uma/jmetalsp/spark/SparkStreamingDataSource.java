package org.uma.jmetalsp.spark;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.UpdateData;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public abstract class SparkStreamingDataSource<D extends UpdateData> implements StreamingDataSource<D>{
	abstract void setStreamingContext(JavaStreamingContext streamingContext) ;
}
