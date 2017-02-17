package org.uma.jmetalsp;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 18/4/16.
 */
public class JMetalSPApplication<
    D extends UpdateData,
    P extends DynamicProblem<? extends Solution<?>, D>,
    A extends DynamicAlgorithm<?, D>,
		S extends StreamingDataSource<D,?>> {

  private List<S> streamingDataSourceList ;
  private List<AlgorithmDataConsumer> algorithmDataConsumerList ;
  private StreamingRuntime streamingRuntime ;

  private P problem ;
  private A algorithm ;
  public JMetalSPApplication() {
    this.streamingDataSourceList = null;
    this.algorithmDataConsumerList = null ;
    this.streamingRuntime = null ;
  }

  public JMetalSPApplication setProblem(P problem) {
    this.problem = problem;

    return this ;
  }

  public JMetalSPApplication setAlgorithm(A algorithm) {
    this.algorithm = algorithm;

    return this ;
  }

  public JMetalSPApplication addStreamingDataSource(S streamingDataSource) {
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

  public JMetalSPApplication setStreamingRuntime(StreamingRuntime runtime) {
    this.streamingRuntime = runtime ;

    return this ;
  }

  public void run() throws IOException, InterruptedException {
    fieldChecking() ;

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

    streamingRuntime.startStreamingDataSources(streamingDataSourceList) ;
    //startStreamingDatasSources();

    /*
    JavaStreamingContext streamingContext = sparkRuntime.getStreamingContext() ;

    for (StreamingDataSource<D> streamingDataSource : streamingDataSourceList) {
      streamingDataSource.setProblem(problem);
      streamingDataSource.start(streamingContext);
    }

    streamingContext.start();
    streamingContext.awaitTermination();
    */

    for (Thread consumerThread : consumerThreadList) {
      consumerThread.join();
    }

    algorithmThread.join();
  }

  private void fieldChecking() {
    if (problem == null) {
      throw new JMetalException("The problem is null") ;
    } else if (algorithm == null) {
      throw new JMetalException("The algorithm algorithm is null") ;
    } else if (algorithmDataConsumerList == null) {
      throw new JMetalException("The algorithm data consumer list is null") ;
    }
  }
}
