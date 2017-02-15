//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalsp.consumer.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.consumer.AlgorithmDataConsumer;
import org.uma.jmetalsp.updatedata.repository.AlgorithmResultData;
import org.uma.khaos.perception.core.Observable;
import org.uma.khaos.perception.core.Observer;

import java.io.File;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class LocalDirectoryOutputConsumer implements AlgorithmDataConsumer<AlgorithmResultData> {
  private String outputDirectoryName;
  private DynamicAlgorithm<?, AlgorithmResultData> dynamicAlgorithm;
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
  public void setAlgorithm(DynamicAlgorithm<?, AlgorithmResultData> algorithm) {
    this.dynamicAlgorithm = algorithm;
  }

  @Override
  public DynamicAlgorithm<?, AlgorithmResultData> getAlgorithm() {
    return dynamicAlgorithm;
  }

  @Override
  public Observer getObserver() {
    return this;
  }


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
  public void update(Observable<?> observable, Object data) {
    System.out.println("Consumer received from " + observable.getName());
    AlgorithmResultData algorithmResultData = (AlgorithmResultData) data;
    //if ("algorithm".equals(observable.getName())) {
    new SolutionListOutput(algorithmResultData.getSolutionList())
            .setSeparator("\t")
            .setFunFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/FUN" + fileCounter + ".tsv"))
            .setVarFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/VAR" + fileCounter + ".tsv"))
            .print();
    fileCounter++;
    //}
  }

}
