package org.uma.jmetalsp.application;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.algorithm.AlgorithmBuilder;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.consumer.AlgorithmDataConsumer;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.ProblemBuilder;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import org.uma.jmetalsp.updatedata.UpdateData;
import org.uma.jmetalsp.util.spark.SparkRuntime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 18/4/16.
 */
public class JMetalSPApplication<
    D extends UpdateData,
    P extends DynamicProblem<? extends Solution<?>, D>,
    A extends DynamicAlgorithm<?>> {

  private AlgorithmBuilder<A, P> algorithmBuilder;
  private ProblemBuilder<P> problemBuilder;
  private List<StreamingDataSource<D>> streamingDataSourceList ;
  private List<AlgorithmDataConsumer> algorithmDataConsumerList ;
  private SparkRuntime sparkRuntime ;

  private P problem ;
  private A algorithm ;
  public JMetalSPApplication() {
    this.streamingDataSourceList = null;
    this.algorithmDataConsumerList = null ;
    this.sparkRuntime = null ;
  }

  public JMetalSPApplication setProblemBuilder(ProblemBuilder<P> problemBuilder) {
    this.problemBuilder = problemBuilder;

    return this ;
  }

  public JMetalSPApplication setAlgorithmBuilder(AlgorithmBuilder<A, P> algorithmBuilder) {
    this.algorithmBuilder = algorithmBuilder;

    return this ;
  }

  public JMetalSPApplication addStreamingDataSource(StreamingDataSource<D> streamingDataSource) {
    if (streamingDataSourceList == null) {
      streamingDataSourceList = new ArrayList<>() ;
    }

    streamingDataSourceList.add(streamingDataSource) ;

    return this ;
  }

  public JMetalSPApplication addAlgorithmDataConsumer(AlgorithmDataConsumer consumer) {
    if (algorithmDataConsumerList == null) {
      algorithmDataConsumerList = new ArrayList<>() ;
    }
    algorithmDataConsumerList.add(consumer) ;

    return this ;
  }

  public JMetalSPApplication setSparkRuntime(SparkRuntime sparkRuntime) {
    this.sparkRuntime = sparkRuntime ;

    return this ;
  }

  public void run() throws IOException, InterruptedException {
    fieldChecking() ;

    problem = problemBuilder.build() ;
    algorithm = algorithmBuilder.build(problem) ;

    for (AlgorithmDataConsumer consumer : algorithmDataConsumerList) {
      consumer.setAlgorithm(algorithm);
    }

    Thread algorithmThread = new Thread(algorithm) ;
    List<Thread> consumerThreadList = new ArrayList<Thread>(algorithmDataConsumerList.size()) ;
    for (AlgorithmDataConsumer consumer : algorithmDataConsumerList) {
      Thread thread = new Thread(consumer) ;
      consumerThreadList.add(thread) ;
      thread.start();
    }
    algorithmThread.start() ;

    JavaStreamingContext streamingContext = sparkRuntime.getStreamingContext() ;

    for (StreamingDataSource<D> streamingDataSource : streamingDataSourceList) {
      streamingDataSource.setProblem(problem);
      streamingDataSource.start(streamingContext);
    }

    streamingContext.start();
    streamingContext.awaitTermination();

    for (Thread consumerThread : consumerThreadList) {
      consumerThread.join();
    }

    algorithmThread.join();
  }

  private void fieldChecking() {
    if (problemBuilder == null) {
      throw new JMetalException("The problem builder is null") ;
    } else if (algorithmBuilder == null) {
      throw new JMetalException("The algorithm builder is null") ;
    } else if (algorithmDataConsumerList == null) {
      throw new JMetalException("The algorithm data consumer list is null") ;
    }
  }
}
