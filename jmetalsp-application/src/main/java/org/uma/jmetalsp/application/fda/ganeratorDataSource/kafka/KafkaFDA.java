package org.uma.jmetalsp.application.fda.ganeratorDataSource.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import java.util.Properties;

/**
 * Created by cris on 27/07/2016.
 */
public class KafkaFDA extends Thread {
  private final KafkaProducer<Integer, Integer> producer;
  private final String topic;
  private final int waitTime;

  public KafkaFDA(int waitTime, String topic,String server,int port) {
    this.topic = topic;
    this.waitTime = waitTime;
    String aux = server+":"+port;
    Properties props = new Properties();
    props.put("bootstrap.servers", aux);
    props.put("client.id", "TSPProducer");
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    producer = new KafkaProducer<Integer, Integer>(props);
  }

  public void run() {
    int messageNo = 1;
    while (true) {
      long startTime = System.currentTimeMillis();
      producer.send(new ProducerRecord<Integer, Integer>(topic, messageNo, messageNo),
              new CallBack(startTime, messageNo, messageNo));
      messageNo++;
      try {

        Thread.sleep(waitTime);
      } catch (Exception e) {

      }

    }
  }

}

class CallBack implements Callback {
  private long startTime;
  private int key;
  private int message;

  public CallBack(long startTime, int key, int message) {
    this.startTime = startTime;
    this.key = key;
    this.message = message;
  }

  /**
   * A callback method the user can implement to provide asynchronous handling
   * of request completion. This method will be called when the record sent to
   * the server has been acknowledged. Exactly one of the arguments will be
   * non-null.
   *
   * @param metadata
   *            The metadata for the record that was sent (i.e. the partition
   *            and offset). Null if an error occurred.
   * @param exception
   *            The exception thrown during processing of this record. Null if
   *            no error occurred.
   */
  public void onCompletion(RecordMetadata metadata, Exception exception) {
    long elapsedTime = System.currentTimeMillis() - startTime;
    if (metadata != null) {
      System.out.println("message(" + key + ", " + message + ") sent to partition(" + metadata.partition() + "), "
              + "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
    } else {
      exception.printStackTrace();
    }
  }

}
