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
import org.uma.jmetalsp.AlgorithmDataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData2;
import org.uma.jmetalsp.observer.Observable;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleSolutionListConsumer implements
        AlgorithmDataConsumer<AlgorithmObservedData2,
                DynamicAlgorithm<?, Observable<AlgorithmObservedData2>>> {
  private DynamicAlgorithm<?, Observable<AlgorithmObservedData2>> dynamicAlgorithm;

  public SimpleSolutionListConsumer(DynamicAlgorithm<?, Observable<AlgorithmObservedData2>> algorithm) {
    this.dynamicAlgorithm = algorithm ;
  }
  /*
  @Override
  public void setAlgorithm(DynamicAlgorithm<?, AlgorithmObservedData> algorithm) {
    this.dynamicAlgorithm = algorithm;
  }
*/
  @Override
  public DynamicAlgorithm<?, Observable<AlgorithmObservedData2>> getAlgorithm() {
    return dynamicAlgorithm;
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

  @Override
  public void update(Observable<AlgorithmObservedData2> observable, AlgorithmObservedData2 data) {
    AlgorithmObservedData2 algorithmResultData = (AlgorithmObservedData2) data;
    System.out.println("Number of generated fronts: " +((List<Integer>) algorithmResultData.getAlgorithmData().get("numberOfIteration")).get(0));
    System.out.println("Size of the front: " + algorithmResultData.getSolutionList().size());
  }
}
