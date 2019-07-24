package org.uma.jmetalsp;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import java.util.List;
import java.util.Properties;

public class KafkaRuntime implements StreamingRuntime {
    private StreamsBuilder streamsBuilder;
    private Properties config;
    private String topic;
    public KafkaRuntime(String topic){
        config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG,"stream-pipe");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.227.26:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,Serdes.Integer().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());
        streamsBuilder = new StreamsBuilder();
        this.topic = topic;
    }

    @Override
    public void startStreamingDataSources(List<StreamingDataSource<?>> streamingDataSourceList) {
        for (StreamingDataSource<?> streamingDataSource : streamingDataSourceList) {
            ((KafkaStreamingDataSource)streamingDataSource).setStreamingBuilder(streamsBuilder);
            ((KafkaStreamingDataSource)streamingDataSource).setTopic(topic);
            streamingDataSource.run();
        }

        //streamingContext.start();
        KafkaStreams streams= new KafkaStreams(streamsBuilder.build(),config);

        try {
            streams.start();;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
