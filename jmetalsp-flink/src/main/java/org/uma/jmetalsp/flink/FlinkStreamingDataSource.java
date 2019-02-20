package org.uma.jmetalsp.flink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.StreamingDataSource;

public interface FlinkStreamingDataSource <D extends ObservedData<?>> extends StreamingDataSource<D> {
   public void setExecutionEnvironment(StreamExecutionEnvironment environment);
   public void setTime(long time);
}
