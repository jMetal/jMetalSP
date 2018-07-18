package org.uma.jmetalsp;

import org.apache.kafka.streams.StreamsBuilder;

import java.io.Serializable;

public interface KafkaStreamingDataSource <D extends ObservedData<?>> extends StreamingDataSource<D>,Serializable{
public void setStreamingBuilder(StreamsBuilder streamingBuilder);
    public void setTopic(String topic);
}
