package org.uma.jmetalsp.application.biobjectivetsp.runner.mod2016;

import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicNSGAIIBuilder;
import org.uma.jmetalsp.application.biobjectivetsp.algorithm.DynamicTSPNSGAII;
import org.uma.jmetalsp.application.biobjectivetsp.problem.DynamicMultiobjectiveTSP;
import org.uma.jmetalsp.application.biobjectivetsp.problem.MultiobjectiveTSPBuilderParsed;
import org.uma.jmetalsp.application.biobjectivetsp.sparkutil.StreamingConfigurationTSP;
import org.uma.jmetalsp.application.biobjectivetsp.streamingDataSource.StreamingDirectoryTSP;
import org.uma.jmetalsp.application.biobjectivetsp.updateData.MultiobjectiveTSPUpdateData;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.util.spark.SparkRuntime;

import java.io.IOException;

/**
 * Created by cris on 08/06/2016.
 */
public class MOD2016Runner {

  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            MultiobjectiveTSPUpdateData,
            DynamicMultiobjectiveTSP,
            DynamicTSPNSGAII> application = new JMetalSPApplication<>();
    StreamingConfigurationTSP streamingConfigurationTSP= new StreamingConfigurationTSP();
    streamingConfigurationTSP.initializeDirectoryTSP("/biobjectivetsp/data2");
    String fileName= "/biobjectivetsp/initialDataFile.txt";
    application
            .setSparkRuntime(new SparkRuntime(1))
            .setProblemBuilder(new MultiobjectiveTSPBuilderParsed(fileName))
            .setAlgorithmBuilder(new DynamicNSGAIIBuilder())
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("/consumer/jMetalSP1"))
            .addStreamingDataSource(new StreamingDirectoryTSP(streamingConfigurationTSP))
            .run();
  }
}
