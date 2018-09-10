package org.uma.jmetalsp.observer.impl;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.uma.jmetalsp.ObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class KafkaObservable<O extends ObservedData> implements Observable<O> {
  private Set<Observer<O>> observers;
  private boolean dataHasChanged;
  private String topicName;
  private Producer<String, String> producer;

  public KafkaObservable(String topicName, O observedDataObject) {
    observers = new HashSet<>();
    dataHasChanged = false;
    this.topicName = topicName;

    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer");
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    this.producer = new KafkaProducer<>(properties) ;
  }

  @Override
  public void register(Observer<O> observer) {
    observers.add(observer);
  }


  @Override
  public void unregister(Observer<O> observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(O data) {
    if (dataHasChanged) {
      System.out.println("Sending data") ;
      producer.send(new ProducerRecord<String, String>(topicName, "0", data.toJson()));
    }
    clearChanged();
  }

  @Override
  public int numberOfRegisteredObservers() {
    return observers.size();
  }

  @Override
  public Collection<Observer<O>> getObservers() {
    return observers ;
  }

  @Override
  public void setChanged() {
    dataHasChanged = true;
  }

  @Override
  public boolean hasChanged() {
    return dataHasChanged;
  }

  @Override
  public void clearChanged() {
    dataHasChanged = false;
  }

}
