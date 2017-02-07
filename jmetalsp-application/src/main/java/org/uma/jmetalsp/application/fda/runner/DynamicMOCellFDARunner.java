package org.uma.jmetalsp.application.fda.runner;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.algorithm.DynamicMOCell;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.fda.algorithm.DynamicMOCellBuilder;
import org.uma.jmetalsp.application.fda.sparkutil.StreamingConfigurationFDA;
import org.uma.jmetalsp.application.fda.streamingDataSource.StreamingKafkaFDA;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.problem.ProblemBuilder;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.problem.fda.FDAUtil;
import org.uma.jmetalsp.problem.fda.fda1.FDA1;
import org.uma.jmetalsp.problem.fda.fda1.FDA1ProblemBuilder;
import org.uma.jmetalsp.problem.fda.fda2.FDA2ProblemBuilder;
import org.uma.jmetalsp.util.spark.SparkRuntime;

import java.io.IOException;

/**
 * Created by cris on 09/01/2017.
 */
public class DynamicMOCellFDARunner {

    public static void main(String[] args) throws IOException, InterruptedException {
      JMetalSPApplication<
              FDAUpdateData,
              FDA1,
              DynamicMOCell<DoubleSolution>> application = new JMetalSPApplication<>();
      StreamingConfigurationFDA streamingConfigurationFDA= new StreamingConfigurationFDA();
      //String kafkaServer="localhost";
      //int kafkaPort=2181;
      String kafkaServer="master.bd.khaos.uma.es";
      int kafkaPort=6667;
      String outputDirectoryName="/opt/consumer/mocell/";
      String kafkaTopic="fdadata";
      String problemName="fda2";
      if (args == null || args.length < 5) {
        System.out.println("Provide the information to kafka and output:");
        System.out.println("    DynamicMOCellFDARunner <kafkaserver> <kafkaport> <kafkatopic> <problem-name> <output-directory-name>");
        System.out.println("");
        return;
      }
      else {
       kafkaServer =args[0];
       kafkaPort=Integer.valueOf(args[1]);
       kafkaTopic=args[2];
       problemName=args[3];
        outputDirectoryName=args[4];
       outputDirectoryName=outputDirectoryName+problemName;
      }
      //Kafka
      //KafkaFDA producerKafka = new KafkaFDA(5000, kafkaTopic,kafkaServer,kafkaPortServidor);
      //producerKafka.start();

      streamingConfigurationFDA.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);
      ProblemBuilder problemBuilder = FDAUtil.load(problemName);
      application
              .setSparkRuntime(new SparkRuntime(2))
              .setProblemBuilder(problemBuilder)
              .setAlgorithmBuilder(new DynamicMOCellBuilder())
              .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
              .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer(outputDirectoryName))
              .addStreamingDataSource(new StreamingKafkaFDA(streamingConfigurationFDA))
              .run();
    }
}
