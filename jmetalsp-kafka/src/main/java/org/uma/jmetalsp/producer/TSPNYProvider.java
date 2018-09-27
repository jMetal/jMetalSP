package org.uma.jmetalsp.producer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

public class TSPNYProvider extends Thread {
    private String topic;
    private KafkaProducer<Integer, byte[]> producer;

    public TSPNYProvider(String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.44.10:9092");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "DemoProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<Integer, byte[]>(props);
    }

    public void run() {
        int count = 0;
        long startTime = System.currentTimeMillis();
        CounterSerializer counterSerializer = new CounterSerializer();
        while (true) {
            count = generateRandomInteger(-10,10);
            Counter counter = new Counter(count);
            byte [] aux= counterSerializer.serializeMessage(counter);
            Future<RecordMetadata> send =

                    producer.send(new ProducerRecord<Integer, byte[]>
                            (topic, count, aux));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    private int generateRandomInteger(int min, int max){
        int result = min + (int)(Math.random() * ((max - ((min)))+1));
        return result;
    }

    public static void main(String[] args) {
        //topic where it will be written the numbers
        TSPNYProvider tspnyProvider = new TSPNYProvider("tsp");
        new Thread(counterProvider).start();
        ;

    }
}
