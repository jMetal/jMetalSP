package org.uma.jmetalsp.consumer;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedSolution;
import org.uma.jmetalsp.observeddata.util.DummySolution;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.KafkaBasedConsumer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalDirectoryOutputConsumer<S extends Solution<?>> implements
        DataConsumer<AlgorithmObservedData> {
  private String outputDirectoryName;
  private int fileCounter = 0;

  /**
   * Constructor
   */
  public LocalDirectoryOutputConsumer(String outputDirectoryName) {
    this.outputDirectoryName = outputDirectoryName;
    createDataDirectory(this.outputDirectoryName);
  }

  private LocalDirectoryOutputConsumer() {
    this.outputDirectoryName = null;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(1000000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void createDataDirectory(String outputDirectoryName) {
    File outputDirectory = new File(outputDirectoryName);

    if (outputDirectory.isDirectory()) {
      for (File file : outputDirectory.listFiles()) {
        file.delete();
      }
      outputDirectory.delete();
      new File(outputDirectoryName).mkdir();
    } else {
      System.out.println("The output directory doesn't exist. Creating ...");
      new File(outputDirectoryName).mkdir();
    }
  }

  @Override
  public void update(Observable<AlgorithmObservedData> observable, AlgorithmObservedData data) {
    List<ObservedSolution<?,?>> solutionList = (List<ObservedSolution<?,?>>)data.getData().get("solutionList") ;

    List<DummySolution<?>> dummySolutionList = new ArrayList<>() ;

    for (ObservedSolution solution : solutionList) {
      dummySolutionList.add(new DummySolution(solution)) ;
    }

    new SolutionListOutput(dummySolutionList)
            .setSeparator("\t")
            .setFunFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/FUN" + fileCounter + ".tsv"))
            .setVarFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/VAR" + fileCounter + ".tsv"))
            .print();
    fileCounter++;
  }

  ////////////////////////////////////////////////
  public static void main(String[] args) {
    String topicName = "prueba-solutionlist-topic-from-main";

    LocalDirectoryOutputConsumer localDirectoryOutputConsumer = new LocalDirectoryOutputConsumer("outputdirectory") ;

    KafkaBasedConsumer<AlgorithmObservedData> localDirectoryKafkaBasedConsumer =
            new KafkaBasedConsumer<>(topicName, localDirectoryOutputConsumer, new AlgorithmObservedData()) ;

    localDirectoryKafkaBasedConsumer.start();

    localDirectoryOutputConsumer.run();
  }
}
