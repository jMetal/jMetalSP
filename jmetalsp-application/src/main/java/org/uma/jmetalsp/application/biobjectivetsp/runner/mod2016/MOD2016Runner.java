package org.uma.jmetalsp.application.biobjectivetsp.runner.mod2016;

import java.io.File;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicNSGAIIBuilder;
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
 * Created by cris on 08/06/2016.
 */
public class MOD2016Runner {
  
  public static void main(String[] args) throws IOException, InterruptedException {
    if (args == null || args.length < 3) {
      System.out.println("Provide the path to the initial file, updates and output:");
      System.out.println("    MOD2016Runner <initial file> <updates directory> <output directory>");
      System.out.println("");
      System.out.println("The initial file should be the output of ParseLinkSpeedQuery.");
      System.out.println("The updates directory should be the path where the updates are generated.");
      return;
    } else {
      File input = new File(args[0]);
      if (!input.exists() || !input.isFile()) {
        System.err.println("Error reading input file.");
        return;
      }
      File updatedir = new File(args[1]);
      if (!updatedir.exists() || !updatedir.isDirectory()) {
        System.err.println("Error reading update directory.");
        return;
      }
    }
    JMetalSPApplication<
        MultiobjectiveTSPUpdateData,
        DynamicMultiobjectiveTSP,
        DynamicTSPNSGAII> application = new JMetalSPApplication<>();
    
    StreamingConfigurationTSP streamingConfigurationTSP= new StreamingConfigurationTSP();
    streamingConfigurationTSP.initializeDirectoryTSP(args[1]);
    String fileName= args[0];
    
    application
        .setSparkRuntime(new SparkRuntime(1))
        .setProblemBuilder(new MultiobjectiveTSPBuilderParsed(fileName))
        .setAlgorithmBuilder(new DynamicNSGAIIBuilder())
        .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
        .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer(args[2]))
        .addStreamingDataSource(new StreamingDirectoryTSP(streamingConfigurationTSP))
        .run();
  }
}
