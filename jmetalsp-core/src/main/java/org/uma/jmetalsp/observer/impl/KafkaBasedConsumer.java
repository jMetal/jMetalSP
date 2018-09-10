package org.uma.jmetalsp.observer.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.observer.Observer;

import java.util.Arrays;
import java.util.Properties;

public class KafkaBasedConsumer<O extends ObservedData> extends Thread {
  private Properties properties;
  private String topicName;
  private O observedDataObject;
  private Observer<O> observer;

  public KafkaBasedConsumer(String topicName, Observer<O> observer, O observedDataObject) {
    this.topicName = topicName;
    this.observedDataObject = observedDataObject;
    this.observer = observer;

    properties = new Properties();

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer");
  }

  @Override
  public void run() {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
    consumer.subscribe(Arrays.asList(topicName));
    int counter = 0;
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
      for (ConsumerRecord<String, String> record : records) {
        O data = (O) observedDataObject.fromJson(record.value());
        observer.update(null, data);
      }
    }
  }
}

