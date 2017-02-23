package org.uma.jmetalsp.application.biobjectivetsp.runner.newyorktraffic;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetalsp.algorithm.AlgorithmBuilder;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.algorithm.DynamicNSGAII;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicTSPNSGAII;
import org.uma.jmetalsp.application.biobjectivetsp.sparkutil.StreamingConfigurationTSP;
import org.uma.jmetalsp.application.biobjectivetsp.streamingDataSource.StreamingDirectoryTSP;
import org.uma.jmetalsp.consumer.AlgorithmDataConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.problem.tsp.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromFiles;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPUpdateData;
import org.uma.jmetalsp.spark.util.spark.SparkRuntime;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;

import java.io.IOException;

/**
 * Created by ajnebro on 22/4/16.
 */
public class DynamicTSPNSGIIRunnerWithStreamingDataSourceB {
  static DynamicMultiobjectiveTSP problem ;
  static DynamicAlgorithm<?> algorithm ;
  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
        MultiobjectiveTSPUpdateData,
                            DynamicMultiobjectiveTSP,
        DynamicTSPNSGAII> application = new JMetalSPApplication<>();

    AlgorithmBuilder<DynamicNSGAII<PermutationSolution<Integer>>, DynamicMultiobjectiveTSP> algorithmBuilder =
        new DynamicNSGAIIBuilder();

    SparkRuntime sparkRuntime = new SparkRuntime(1) ;

    //SparkConf sparkConf = new SparkConf().setAppName("SparkClient");
    //JavaSparkContext sparkContext = new JavaSparkContext(sparkConf) ;
    //JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(20));
    JavaStreamingContext streamingContext = sparkRuntime.getStreamingContext() ;

    problem = new MultiobjectiveTSPBuilderFromFiles("kroA100.tsp", "kroB100.tsp").build() ;
    algorithm = new DynamicNSGAIIBuilder().build(problem) ;

    AlgorithmDataConsumer consumer = new SimpleSolutionListConsumer() ;
    consumer.setAlgorithm(algorithm);

    Thread consumerThread = new Thread(consumer) ;
    consumerThread.start();

    Thread algorithmThread = new Thread(algorithm) ;
    algorithmThread.start() ;

    // Setting text file streams: one for the new incoming data and other one for the FUN files
    JavaDStream<String> lines = streamingContext.textFileStream("/Users/ajnebro/Personal/Investigacion/EnMarcha/mod2016/data");

      StreamingConfigurationTSP streamingConfigurationTSP1= new StreamingConfigurationTSP();
      streamingConfigurationTSP1.initializeDirectoryTSP("/Users/ajnebro/Personal/Investigacion/EnMarcha/mod2016/data");
    StreamingDataSource<MultiobjectiveTSPUpdateData> directorySource =
           new StreamingDirectoryTSP(streamingConfigurationTSP1) ;

    directorySource.setProblem(problem);
    directorySource.start(sparkRuntime.getStreamingContext());

    sparkRuntime.start();

    algorithmThread.join();


 //   StreamingDataSource<MultiobjectiveTSPUpdateData> directorySource =
 //       new StreamingDirectoryTSP("/Users/ajnebro/Personal/Investigacion/EnMarcha/mod2016/data") ;

    /*
    application
        .setProblemBuilder(new MultiobjectiveTSPBuilderFromFiles("kroA100.tsp", "kroB100.tsp"))
        .setAlgorithmBuilder(algorithmBuilder)
        .addAlgorithmDataConsumer(new PlotSolutionListConsumer())
     //   .addStreamingDataSource(directorySource)
     //   .setRuntime(new SparkRuntime(4))
        .run(streamingContext);
        */
  }
}
