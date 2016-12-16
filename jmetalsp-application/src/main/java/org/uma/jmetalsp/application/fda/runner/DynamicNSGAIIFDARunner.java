package org.uma.jmetalsp.application.fda.runner;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.algorithm.DynamicNSGAII;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.fda.algorithm.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.fda.sparkutil.StreamingConfigurationFDA;
import org.uma.jmetalsp.application.fda.streamingDataSource.StreamingKafkaFDA;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.problem.fda.fda1.FDA1;
import org.uma.jmetalsp.problem.fda.fda2.FDA2ProblemBuilder;
import org.uma.jmetalsp.util.spark.SparkRuntime;

import java.io.IOException;

/**
 * Created by cris on 06/09/2016.
 */
public class DynamicNSGAIIFDARunner {
  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            FDAUpdateData,
            FDA1,
            DynamicNSGAII<DoubleSolution>> application = new JMetalSPApplication<>();
    StreamingConfigurationFDA streamingConfigurationFDA= new StreamingConfigurationFDA();
     String kafkaServer="localhost";
    int kafkaPort=2181;
    int kafkaPortServidor=9092;
    String kafkaTopic="fdadata";

    //Kafka
    //KafkaFDA producerKafka = new KafkaFDA(5000, kafkaTopic,kafkaServer,kafkaPortServidor);
    //producerKafka.start();

    streamingConfigurationFDA.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);

    application
            .setSparkRuntime(new SparkRuntime(2))
            .setProblemBuilder(new FDA2ProblemBuilder(31,2))
            //  .setProblemBuilder(new MultiobjectiveTSPBuilderFromFiles("/home/hdfs/tsp/kroA100.tsp", "/home/hdfs/tsp/kroB100.tsp"))
            .setAlgorithmBuilder(new DynamicNSGAIIBuilder())
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("/Users/cristobal/Documents/tesis/fdansga2"))
            .addStreamingDataSource(new StreamingKafkaFDA(streamingConfigurationFDA))
            // .addStreamingDataSource(new StreamingKafkaTSP(streamingConfigurationTSP1))
            .run();
  }
}
