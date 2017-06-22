package org.uma.jmetalsp.consumer;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.io.File;
import java.util.List;

/**
 * This consumer receives lists of solutions and store them in a directory
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class LocalDirectoryOutputConsumer<S extends Solution<?>> implements
        DataConsumer<SingleObservedData<List<S>>> {
  private String outputDirectoryName;
  private DynamicAlgorithm<?, Observable<SingleObservedData<List<S>>>> dynamicAlgorithm;
  private int fileCounter = 0;

  /**
   * Constructor
   */
  public LocalDirectoryOutputConsumer(String outputDirectoryName,
                                      DynamicAlgorithm<?, Observable<SingleObservedData<List<S>>>> algorithm) {
    this.outputDirectoryName = outputDirectoryName;
    this.dynamicAlgorithm = algorithm ;
    createDataDirectory(this.outputDirectoryName);
  }

  private LocalDirectoryOutputConsumer() {
    this.outputDirectoryName = null;
  }

/*
  @Override
  public void setAlgorithm(DynamicAlgorithm<?, AlgorithmObservedData> algorithm) {
    this.dynamicAlgorithm = algorithm;
  }


   public Observer getObserver() {
    return this;
  }
*/
  @Override
  public void run() {
    if (dynamicAlgorithm == null) {
      throw new JMetalException("The algorithm is null");
    }

    dynamicAlgorithm.getObservable().register(this);


    while (true) {
      try {
        Thread.sleep(1000000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public Observable<SingleObservedData<List<S>>> getObservable() {
    return dynamicAlgorithm.getObservable();
  }

  private void createDataDirectory(String outputDirectoryName) {
    File outputDirectory = new File(outputDirectoryName);

    if (outputDirectory.isDirectory()) {
      System.out.println("The output directory exists. Deleting and creating ...");
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
  public void update(Observable<SingleObservedData<List<S>>> observable, SingleObservedData<List<S>> data) {
    new SolutionListOutput(data.getData())
            .setSeparator("\t")
            .setFunFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/FUN" + fileCounter + ".tsv"))
            .setVarFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/VAR" + fileCounter + ".tsv"))
            .print();
    fileCounter++;
  }
}
