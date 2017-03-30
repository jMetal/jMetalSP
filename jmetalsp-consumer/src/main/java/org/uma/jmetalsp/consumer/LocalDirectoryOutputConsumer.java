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

package org.uma.jmetalsp.consumer;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalsp.AlgorithmDataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.Observer;

import java.io.File;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class LocalDirectoryOutputConsumer implements
        AlgorithmDataConsumer<AlgorithmObservedData, DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>>> {
  private String outputDirectoryName;
  private DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>> dynamicAlgorithm;
  private int fileCounter = 0;

  /**
   * Constructor
   */
  public LocalDirectoryOutputConsumer(String outputDirectoryName, DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>> algorithm) {
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
*/
  @Override
  public DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>> getAlgorithm() {
    return dynamicAlgorithm;
  }

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
  public void update(Observable<AlgorithmObservedData> observable, AlgorithmObservedData data) {
    AlgorithmObservedData algorithmResultData = (AlgorithmObservedData) data;
    new SolutionListOutput(algorithmResultData.getSolutionList())
            .setSeparator("\t")
            .setFunFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/FUN" + fileCounter + ".tsv"))
            .setVarFileOutputContext(new DefaultFileOutputContext(outputDirectoryName + "/VAR" + fileCounter + ".tsv"))
            .print();
    fileCounter++;
    //}
  }

}
