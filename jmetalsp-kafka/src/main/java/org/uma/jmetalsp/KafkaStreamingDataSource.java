package org.uma.jmetalsp;

import org.apache.kafka.streams.StreamsBuilder;

public interface KafkaStreamingDataSource <D extends ObservedData<?>> extends StreamingDataSource<D>{
    public void setStreamingBuilder(StreamsBuilder streamingBuilder);
    public void setTopic(String topic);
}
