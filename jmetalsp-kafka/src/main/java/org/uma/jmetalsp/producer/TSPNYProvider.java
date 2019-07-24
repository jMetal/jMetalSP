package org.uma.jmetalsp.producer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.Future;
import org.json.JSONArray;

public class TSPNYProvider extends Thread {

  private String topic;
  private KafkaProducer<Integer, String> producer;

  public TSPNYProvider(String topic) {
    this.topic = topic;
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, "DemoProducer");
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producer = new KafkaProducer<Integer, String>(props);
  }

  public JSONArray readJsonFromUrl(String url) {
    JSONArray json = null;
    try {
      InputStream is = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      json = new JSONArray(jsonText);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return json;
  }

  private static String readAll(Reader rd) {
    String result = "";
    try {
      StringBuilder sb = new StringBuilder();
      int cp;
      while ((cp = rd.read()) != -1) {
        sb.append((char) cp);
      }
      result = sb.toString();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public void run() {
    int count = 0;
    long startTime = System.currentTimeMillis();

    while (true) {
      count = generateRandomInteger(0, 1000);
      JSONArray json = null;
      json = readJsonFromUrl("http://data.cityofnewyork.us/resource/i4gi-tjb9.json");
      String messageStr = json.toString();

      Future<RecordMetadata> send =
          producer.send(new ProducerRecord<Integer, String>
              (topic, count, messageStr));
      try {
        Thread.sleep(20000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private int generateRandomInteger(int min, int max) {
    int result = min + (int) (Math.random() * ((max - ((min))) + 1));
    return result;
  }

  public static void main(String[] args) {
    TSPNYProvider tspNYProvider = new TSPNYProvider("tsp");
    new Thread(tspNYProvider).start();
  }
}
