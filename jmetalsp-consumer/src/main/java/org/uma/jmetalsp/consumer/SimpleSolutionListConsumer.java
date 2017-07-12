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

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;

/**
 * This consumer receives a list of solutions and prints information about it
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleSolutionListConsumer<S extends Solution<?>> implements
        DataConsumer<AlgorithmObservedData<S>> {

  private DynamicAlgorithm<?, AlgorithmObservedData<S>> dynamicAlgorithm;

  public SimpleSolutionListConsumer(DynamicAlgorithm<?, AlgorithmObservedData<S>> algorithm) {
    this.dynamicAlgorithm = algorithm ;
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
  public void update(Observable<AlgorithmObservedData<S>> observable, AlgorithmObservedData<S> data) {
    System.out.println("Size of the list of solutions: " + data.getData().size());
    System.out.println("First solution: " + data.getData().get(0)) ;
  }
}
