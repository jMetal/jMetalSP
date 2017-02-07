package org.uma.jmetalsp.application.fda.generatorDataSource.kafka;

/**
 * Created by cristobal on 28/7/16.
 */
public class GeneratorTimeKafka {

    public static void main(String[] args) {
       String topic = "fdadata";
        String server = "localhost";
        int waitTime= 5000;
        int port = 9092;
        String clientId="FDAClient";
        System.out.println("Creating producer..");
      if (args == null || args.length != 4) {
        System.out.println("Provide kafka information and time");
        System.out.println("    DynamicMOCellRunner <time> <topic> <server> <port>");
        System.out.println("");
        return;
      }else{
        waitTime= Integer.parseInt(args[0]);
        topic= args[1];
        server = args[2];
        port= Integer.parseInt(args[3]);
      }
        KafkaFDA producerThread = new KafkaFDA(waitTime, topic,server,port,clientId);
        producerThread.start();
        System.out.println("Producer done");

    }
}
