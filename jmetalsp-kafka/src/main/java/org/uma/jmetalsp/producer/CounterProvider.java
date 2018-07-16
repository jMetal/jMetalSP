package org.uma.jmetalsp.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.uma.jmetalsp.problem.fda.FDA;

import java.util.Properties;
import java.util.concurrent.Future;

public class CounterProvider  extends Thread{
    private  String topic;
    private KafkaProducer<Integer,Integer> producer;
    public CounterProvider(String topic){
        this.topic = topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("client.id", "DemoProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        producer = new KafkaProducer<Integer, Integer>(props);
    }
    public void run(){
        int count = 0;
        long startTime = System.currentTimeMillis();
        while (true){
            Future<RecordMetadata> send =
            producer.send(new ProducerRecord<Integer, Integer>
                    (topic,count,count), new ProducerCallBack(startTime, count, "Count ->" + count) );

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }
    class ProducerCallBack implements Callback {
        private long startTime;
        private int key;
        private String message;

        public ProducerCallBack(long startTime, int key, String message) {
            this.startTime = startTime;
            this.key = key;
            this.message = message;
        }

        /**
         * A callback method the user can implement to provide asynchronous handling of request completion. This method will
         * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
         * non-null.
         *
         * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
         *                  occurred.
         * @param exception The exception thrown during processing of this record. Null if no error occurred.
         */
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (metadata != null) {
                System.out.println(
                        "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
                                "), " +
                                "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
            } else {
                exception.printStackTrace();
            }
        }
    }

}
