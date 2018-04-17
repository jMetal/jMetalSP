package org.uma.jmetalsp;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetalsp.observer.Observer;

/**
 *
 * @author Antonio J. Nebro
 *
 * @param <S> Solution (encoding)
 * @param <P> Problem
 * @param <A> Algorithm
 */
public class JMetalSPApplication<
        S extends Solution<?>,
        P extends DynamicProblem<S, ?>,
        A extends DynamicAlgorithm<?, ? extends ObservedData<?>>> {

  private List<StreamingDataSource<?>> streamingDataSourceList;
  private List<DataConsumer<?>> algorithmDataConsumerList;
  private StreamingRuntime streamingRuntime;

  private P problem;
  private A algorithm;

  public JMetalSPApplication() {
    this.streamingDataSourceList = null;
    this.algorithmDataConsumerList = null;
    this.streamingRuntime = null;
  }

  public JMetalSPApplication(P problem,A algorithm) {
    this();
    this.problem = problem;
    this.algorithm = algorithm;
  }

  public JMetalSPApplication setProblem(P problem) {
    this.problem = problem;

    return this;
  }

  public JMetalSPApplication setAlgorithm(A algorithm) {
    this.algorithm = algorithm;

    return this;
  }

  public JMetalSPApplication addStreamingDataSource(StreamingDataSource<?> streamingDataSource,Observer observer) {
    if (streamingDataSourceList == null) {
      streamingDataSourceList = new ArrayList<>();
    }
      streamingDataSource.getObservable().register(observer);

    streamingDataSourceList.add(streamingDataSource);

    return this;
  }

  public JMetalSPApplication addAlgorithmDataConsumer(DataConsumer<?> consumer) {
    if (algorithmDataConsumerList == null) {
      algorithmDataConsumerList = new ArrayList<>();
    }

    this.algorithm.getObservable().register((Observer)consumer);
    algorithmDataConsumerList.add(consumer);

    return this;
  }

  public JMetalSPApplication setStreamingRuntime(StreamingRuntime runtime) {
    this.streamingRuntime = runtime;

    return this;
  }

  public void run() throws IOException, InterruptedException {
    fieldChecking();

    Thread algorithmThread = new Thread(algorithm);
    List<Thread> consumerThreadList = new ArrayList<Thread>(algorithmDataConsumerList.size());
    for (DataConsumer<?> consumer : algorithmDataConsumerList) {
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
