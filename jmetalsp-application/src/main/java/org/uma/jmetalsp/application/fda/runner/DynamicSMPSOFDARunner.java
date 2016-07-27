package  org.uma.jmetalsp.application.fda.runner;



import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetalsp.algorithm.DynamicSMPSO;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.fda.algorithm.DynamicSMPSOBuilder;
import org.uma.jmetalsp.application.fda.ganeratorDataSource.kafka.KafkaFDA;
import org.uma.jmetalsp.application.fda.problem.FDA1;
import org.uma.jmetalsp.application.fda.problem.FDA1ProblemBuilder;
import org.uma.jmetalsp.application.fda.problem.FDAUpdateData;
import org.uma.jmetalsp.application.fda.sparkutil.StreamingConfigurationFDA;
import org.uma.jmetalsp.application.fda.streamingDataSource.StreamingKafkaFDA;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.util.spark.SparkRuntime;

import java.io.IOException;

/**
 * Created by cris on 20/07/2016.
 */
public class DynamicSMPSOFDARunner {
  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            FDAUpdateData,
            FDA1,
            DynamicSMPSO> application = new JMetalSPApplication<>();
    StreamingConfigurationFDA streamingConfigurationFDA= new StreamingConfigurationFDA();
     String kafkaServer="localhost";
    int kafkaPort=2181;
    int kafkaPortServidor=9092;
    String kafkaTopic="fdadata";

    //Kafka
    KafkaFDA producerKafka = new KafkaFDA(5000, kafkaTopic,kafkaServer,kafkaPortServidor);
    producerKafka.start();

    streamingConfigurationFDA.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);
    application
            .setSparkRuntime(new SparkRuntime(20))
            .setProblemBuilder(new FDA1ProblemBuilder(100,2))
            //  .setProblemBuilder(new MultiobjectiveTSPBuilderFromFiles("/home/hdfs/tsp/kroA100.tsp", "/home/hdfs/tsp/kroB100.tsp"))
            .setAlgorithmBuilder(new DynamicSMPSOBuilder(new FDA1(),new CrowdingDistanceArchive<DoubleSolution>(100)))
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("/opt/consumer/jMetalSP1"))
            .addStreamingDataSource(new StreamingKafkaFDA(streamingConfigurationFDA))
            // .addStreamingDataSource(new StreamingKafkaTSP(streamingConfigurationTSP1))
            .run();
  }
}
