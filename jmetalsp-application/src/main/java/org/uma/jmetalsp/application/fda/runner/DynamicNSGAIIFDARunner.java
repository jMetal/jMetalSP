package org.uma.jmetalsp.application.fda.runner;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.algorithm.DynamicNSGAII;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.fda.algorithm.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.fda.sparkutil.StreamingConfigurationFDA;
import org.uma.jmetalsp.application.fda.streamingDataSource.StreamingKafkaFDA;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.problem.ProblemBuilder;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.problem.fda.FDAUtil;
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
    String kafkaServer="master.bd.khaos.uma.es";
    int kafkaPort=6667;
    String outputDirectoryName="/opt/consumer/nsga2/";
    String kafkaTopic="fdadata";
    String problemName="fda2";
    if (args == null || args.length < 5) {
      System.out.println("Provide the information to kafka and output:");
      System.out.println("    DynamicNSGAIIFDARunner <kafkaserver> <kafkaport> <kafkatopic> <problem-name> <output-directory-name>");
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

    streamingConfigurationFDA.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);
    ProblemBuilder problemBuilder = FDAUtil.load(problemName);

    streamingConfigurationFDA.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);

    application
            .setSparkRuntime(new SparkRuntime(2))
            .setProblemBuilder(problemBuilder)
            .setAlgorithmBuilder(new DynamicNSGAIIBuilder())
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer(outputDirectoryName))
            .addStreamingDataSource(new StreamingKafkaFDA(streamingConfigurationFDA))
            .run();
  }
}
