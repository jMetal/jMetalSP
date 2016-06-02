package org.uma.jmetalsp.consumer.impl;

import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.consumer.AlgorithmDataConsumer;

import java.io.File;
import java.util.List;

/**
 * Created by ajnebro on 25/4/16.
 */
public class LocalDirectoryOutputConsumer implements AlgorithmDataConsumer {
  private String outputDirectoryName ;

  /**
   * Constructor
   *
   * @param outputDirectoryName
   */
  public LocalDirectoryOutputConsumer(String outputDirectoryName) {
    this.outputDirectoryName = outputDirectoryName ;
  }

  private LocalDirectoryOutputConsumer() {
    this.outputDirectoryName = null ;
  }

  private DynamicAlgorithm<?> dynamicAlgorithm ;

  @Override
  public void setAlgorithm(DynamicAlgorithm<?> algorithm) {
    this.dynamicAlgorithm = algorithm ;
  }

  @Override
  public DynamicAlgorithm<?> getAlgorithm() {
    return dynamicAlgorithm ;
  }

  @Override
  public void run() {
    if (dynamicAlgorithm == null) {
      throw new JMetalException("The algorithm is null") ;
    }

    createDataDirectory(outputDirectoryName);

    MeasureManager measureManager = dynamicAlgorithm.getMeasureManager() ;

    if (measureManager == null) {
      throw new JMetalException("Error capturing measure manager") ;
    }

    BasicMeasure<List<DoubleSolution>> solutionListMeasure =
        (BasicMeasure<List<DoubleSolution>>) measureManager.<List<DoubleSolution>> getPushMeasure("currentPopulation");

    solutionListMeasure.register(new LocalDirectoryOutputConsumer.Listener(outputDirectoryName));
    while(true){
      try {
        Thread.sleep(1000000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void createDataDirectory(String outputDirectoryName) {
    File outputDirectory = new File(outputDirectoryName) ;

    if (outputDirectory.isDirectory()) {
      System.out.println("The output directory exists. Deleting and creating ...") ;
      for (File file : outputDirectory.listFiles()) {
        file.delete();
      }
      outputDirectory.delete() ;
      new File(outputDirectoryName).mkdir() ;
    } else {
      System.out.println("The output directory doesn't exist. Creating ...") ;
      new File(outputDirectoryName).mkdir() ;
    }
  }

  private static class Listener<S extends Solution<?>>
      implements MeasureListener<List<S>> {

    private int counter = 0;
    private String outputDirectoryName;

    public Listener(String outputDirectoryName) {
      this.outputDirectoryName = outputDirectoryName;
    }

    @Override
    synchronized public void measureGenerated(List<S> solutions) {
      new SolutionListOutput(solutions)
          .setSeparator("\t")
          .setFunFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/FUN" + counter + ".tsv"))
          .setVarFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/VAR" + counter + ".tsv"))
          .print();
        counter++;
    }
  }
}
