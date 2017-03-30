package org.uma.jmetalsp;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.perception.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Antonio J. Nebro
 *
 * @param <SD> Streaming Data (data produced by the streaming data sources)
 * @param <AD> Algorithm Data (data produced by the algorithm)
 * @param <P> Problem
 * @param <A> Algorithm
 * @param <S> Streaming data source
 * @param <C> Algorithm data consumer
 */
public class JMetalSPApplication<
        SD extends ObservedData,
        AD extends ObservedData,
        P extends DynamicProblem<? extends Solution<?>, SD>,
        A extends DynamicAlgorithm<?, AD, ? extends Observable<AD>>,
        S extends StreamingDataSource<SD, ? extends Observable<SD>>,
        C extends AlgorithmDataConsumer<AD, A>> {

  private List<S> streamingDataSourceList;
  private List<C> algorithmDataConsumerList;
  private StreamingRuntime streamingRuntime;

  private P problem;
  private A algorithm;

  public JMetalSPApplication() {
    this.streamingDataSourceList = null;
    this.algorithmDataConsumerList = null;
    this.streamingRuntime = null;
  }

  public JMetalSPApplication setProblem(P problem) {
    this.problem = problem;

    return this;
  }

  public JMetalSPApplication setAlgorithm(A algorithm) {
    this.algorithm = algorithm;

    return this;
  }

  public JMetalSPApplication addStreamingDataSource(S streamingDataSource) {
    if (streamingDataSourceList == null) {
      streamingDataSourceList = new ArrayList<>();
    }

    streamingDataSourceList.add(streamingDataSource);

    return this;
  }

  public JMetalSPApplication addAlgorithmDataConsumer(C consumer) {
    if (algorithmDataConsumerList == null) {
      algorithmDataConsumerList = new ArrayList<>();
    }
    algorithmDataConsumerList.add(consumer);

    return this;
  }

  public JMetalSPApplication setStreamingRuntime(StreamingRuntime runtime) {
    this.streamingRuntime = runtime;

    return this;
  }

  public void run() throws IOException, InterruptedException {
    fieldChecking();

    //for (C consumer : algorithmDataConsumerList) {
    //  consumer.setAlgorithm(algorithm);
    //}

    Thread algorithmThread = new Thread(algorithm);
    List<Thread> consumerThreadList = new ArrayList<Thread>(algorithmDataConsumerList.size());
    for (C consumer : algorithmDataConsumerList) {
      Thread thread = new Thread(consumer);
      consumerThreadList.add(thread);
      thread.start();
    }
    algorithmThread.start();

    streamingRuntime.startStreamingDataSources(streamingDataSourceList);

    for (Thread consumerThread : consumerThreadList) {
      consumerThread.join();
    }

    algorithmThread.join();
  }

  private void fieldChecking() {
    if (problem == null) {
      throw new JMetalException("The problem is null");
    } else if (algorithm == null) {
      throw new JMetalException("The algorithm algorithm is null");
    } else if (algorithmDataConsumerList == null) {
      throw new JMetalException("The algorithm data consumer list is null");
    }
  }
}
