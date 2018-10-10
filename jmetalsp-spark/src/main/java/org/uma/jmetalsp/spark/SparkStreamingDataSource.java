package org.uma.jmetalsp.spark;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.StreamingDataSource;

/**
 * Interface for Spark based streaming data sources
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface SparkStreamingDataSource<D extends ObservedData<?>> extends StreamingDataSource<D> {
  void setStreamingContext(JavaStreamingContext streamingContext) ;
}

