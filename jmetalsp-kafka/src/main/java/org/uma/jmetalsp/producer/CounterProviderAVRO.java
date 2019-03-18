package org.uma.jmetalsp.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.uma.jmetalsp.serialization.DataDeserializer;
import org.uma.jmetalsp.serialization.DataSerializer;
import org.uma.jmetalsp.serialization.counter.Counter;


import java.util.Properties;
import java.util.concurrent.Future;

public class CounterProviderAVRO extends Thread{
   private  String topic;
    private KafkaProducer<Integer,byte[]> producer;
    public CounterProviderAVRO(String topic){
        this.topic = topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.227.26:9092");
        props.put("client.id", "CounterProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<Integer, byte[]>(props);
    }
    public void run() {
        int count = 0;
        long startTime = System.currentTimeMillis();
        DataSerializer<Counter> counterSerializer = new DataSerializer();
        while (true) {
            Counter counter = new Counter(count);
            byte [] aux= counterSerializer.serializeMessage(counter,"avsc/Counter.avsc");
            Future<RecordMetadata> send =

                    producer.send(new ProducerRecord<Integer, byte[]>
                            (topic, count, aux));
            System.out.println("Kafka enviado : "+count);
            count++;

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    public static void main (String [] args){
        CounterProviderAVRO counterProvider = new CounterProviderAVRO("counter");
        new Thread(counterProvider).start();;

    }

}
