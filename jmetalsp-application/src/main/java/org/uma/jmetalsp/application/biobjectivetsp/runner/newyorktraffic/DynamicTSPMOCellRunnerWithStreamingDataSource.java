package org.uma.jmetalsp.application.biobjectivetsp.runner.newyorktraffic;

import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicMOCellBuilder;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicTSPNSGAII;
import org.uma.jmetalsp.problem.tsp.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderParsed;
import org.uma.jmetalsp.application.biobjectivetsp.sparkutil.StreamingConfigurationTSP;
import org.uma.jmetalsp.application.biobjectivetsp.streamingDataSource.StreamingDirectoryTSP;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPUpdateData;
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
    String kafkaServer="master.bd.khaos.uma.es";
    int kafkaPort=6667;
    String kafkaTopic="tspdata";
    String hdfsIp="master.khaos.uma.es";
    int hdfsPort = 8020;
    String fileName= "/tsp/initialDataFile.txt";
    String updateDirectory="/tsp/data2";
    String outputDirectory="/opt/consumer/jMetalSP1";
    if (args == null || args.length < 6) {
      System.out.println("Provide the path to the initial file, updates and output:");
      System.out.println("    DynamicMOCellRunner <initial file> <updates directory> <output directory> <kafkaserver> <kafkaport> <kafkatopic>");
      System.out.println("");
      System.out.println("The initial file should be the output of ParseLinkSpeedQuery.");
      System.out.println("The updates directory should be the path where the updates are generated.");
    }else{
      fileName=args[0];
      updateDirectory=args[1];
      outputDirectory=args[2];
      kafkaServer=args[3];
      kafkaPort=Integer.parseInt(args[4]);
      kafkaTopic=args[5];
    }

    StreamingConfigurationTSP streamingConfigurationTSP1= new StreamingConfigurationTSP();
    streamingConfigurationTSP1.initializeDirectoryTSP(updateDirectory);

    streamingConfigurationTSP1.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);
    //StreamingConfigurationTSP streamingConfigurationTSP2= new StreamingConfigurationTSP();
    // streamingConfigurationTSP2.initializeDirectoryTSP("/tsp/data3");

    application
        .setStreamingRuntime(new SparkRuntime(1))
        .setProblemBuilder(new MultiobjectiveTSPBuilderParsed(hdfsIp,hdfsPort,fileName))
        //  .setProblemBuilder(new MultiobjectiveTSPBuilderFromFiles("/home/hdfs/tsp/kroA100.tsp", "/home/hdfs/tsp/kroB100.tsp"))
        .setAlgorithmBuilder(new DynamicMOCellBuilder())
        .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
        .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer(outputDirectory))
        //.addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("/opt/consumer/jMetalSP2"))
        .addStreamingDataSource(new StreamingDirectoryTSP(streamingConfigurationTSP1))
       //  .addStreamingDataSource(new StreamingKafkaTSP(streamingConfigurationTSP1))
        .run();
  }
}
