package org.uma.jmetalsp.observer.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.observer.Observer;
import org.uma.jmetalsp.serialization.DataDeserializer;
import org.uma.jmetalsp.serialization.DataSerializer;

import java.util.Arrays;
import java.util.Properties;

public class KafkaBasedConsumer<O extends ObservedData<?>> extends Thread {
  private Properties properties;
  private String topicName;
  private O observedDataObject;
  private Observer<O> observer;
  private DataDeserializer deserializer;
  private String pathAVRO;

  public KafkaBasedConsumer(String topicName, Observer<O> observer, O observedDataObject) {
    this.topicName = topicName;
    this.observedDataObject = observedDataObject;
    this.observer = observer;

    properties = new Properties();

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.227.26:9092");
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer");
  }
  public KafkaBasedConsumer(String topicName, Observer<O> observer,O observedDataObject,String pathAvro) {
    this.topicName = topicName;
    this.pathAVRO = pathAvro;
    this.deserializer = new DataDeserializer();
    this.observer = observer;
    this.observedDataObject =observedDataObject;
    properties = new Properties();

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.227.26:9092");//192.168.48.222:29092
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
  }

  @Override
  public void run() {
    if(pathAVRO==null) {
      KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
      consumer.subscribe(Arrays.asList(topicName));
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
        for (ConsumerRecord<String, String> record : records) {
          if(record!=null && record.value()!=null) {
            O data = (O) observedDataObject.fromJson(record.value());
            observer.update(null, data);
          }
        }
      }
    }else{
      KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(properties);
      consumer.subscribe(Arrays.asList(topicName));
      while (true) {
        ConsumerRecords<String, byte[]> records = consumer.poll(Long.MAX_VALUE);
        for (ConsumerRecord<String, byte[]> record : records) {
          if(record!=null && record.value()!=null) {
            O data = (O) deserializer.deserialize(record.value(), pathAVRO);
            observer.update(null, data);
          }
        }
      }

    }

  }
}

