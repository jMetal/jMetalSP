package org.uma.jmetalsp;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import java.util.List;
import java.util.Properties;

public class KafkaRuntime implements StreamingRuntime {
    private StreamsBuilder streamsBuilder;
    private Properties config;
    public KafkaRuntime(){
        config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG,"stream-pipe");

    }

    @Override
    public void startStreamingDataSources(List<StreamingDataSource<?>> streamingDataSourceList) {
        for (StreamingDataSource<?> streamingDataSource : streamingDataSourceList) {
            ((KafkaStreamingDataSource)streamingDataSource).setStreamingBuilder(streamsBuilder);
            streamingDataSource.run();
        }

        //streamingContext.start();
        KafkaStreams streams= new
        try {
            streamingContext.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
