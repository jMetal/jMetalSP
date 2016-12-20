package org.uma.jmetalsp.application.biobjectivetsp.runner.asoc2016;

import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicMOCellBuilder;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicTSPNSGAII;
import org.uma.jmetalsp.application.biobjectivetsp.problem.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.application.biobjectivetsp.problem.MultiobjectiveTSPBuilderParsed;
import org.uma.jmetalsp.application.biobjectivetsp.sparkutil.StreamingConfigurationTSP;
import org.uma.jmetalsp.application.biobjectivetsp.streamingDataSource.StreamingDirectoryTSP;
import org.uma.jmetalsp.application.biobjectivetsp.streamingDataSource.StreamingKafkaTSP;
import org.uma.jmetalsp.application.biobjectivetsp.updateData.MultiobjectiveTSPUpdateData;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.util.spark.SparkRuntime;

import java.io.IOException;

/**
 * Created by ajnebro on 22/4/16 .
 */
public class DynamicTSPMOCellRunnerWithStreamingDataSource {
  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
        MultiobjectiveTSPUpdateData,
        DynamicMultiobjectiveTSP,
        DynamicTSPNSGAII> application = new JMetalSPApplication<>();
    StreamingConfigurationTSP streamingConfigurationTSP1= new StreamingConfigurationTSP();
    streamingConfigurationTSP1.initializeDirectoryTSP("/tsp/data2");
    String kafkaServer="master.bd.khaos.uma.es";
    int kafkaPort=6667;
    String kafkaTopic="tspdata";
    streamingConfigurationTSP1.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);
    //StreamingConfigurationTSP streamingConfigurationTSP2= new StreamingConfigurationTSP();
    // streamingConfigurationTSP2.initializeDirectoryTSP("/tsp/data3");
    String hdfsIp="master.semantic.khaos.uma.es";
            //"master.khaos.uma.es";
    int hdfsPort = 8020;
    String fileName= "/tsp/initialDataFile.txt";
    application
        .setSparkRuntime(new SparkRuntime(1))
        .setProblemBuilder(new MultiobjectiveTSPBuilderParsed(hdfsIp,hdfsPort,fileName))
        //  .setProblemBuilder(new MultiobjectiveTSPBuilderFromFiles("/home/hdfs/tsp/kroA100.tsp", "/home/hdfs/tsp/kroB100.tsp"))
        .setAlgorithmBuilder(new DynamicMOCellBuilder())
        .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
        .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("/opt/consumer/jMetalSP1"))
        //.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("/opt/consumer/jMetalSP2"))
        .addStreamingDataSource(new StreamingDirectoryTSP(streamingConfigurationTSP1))
       //  .addStreamingDataSource(new StreamingKafkaTSP(streamingConfigurationTSP1))
        .run();
  }
}
