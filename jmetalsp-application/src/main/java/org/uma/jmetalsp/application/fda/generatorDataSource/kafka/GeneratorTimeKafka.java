package org.uma.jmetalsp.application.fda.generatorDataSource.kafka;

/**
 * Created by cristobal on 28/7/16.
 */
public class GeneratorTimeKafka {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String topic = "fdadata";//args[0];
        System.out.println("Creando productor..");
        KafkaFDA producerThread = new KafkaFDA(5000, topic,"localhost",9092);
        producerThread.start();
        System.out.println("Produtor creado");

    }
}
