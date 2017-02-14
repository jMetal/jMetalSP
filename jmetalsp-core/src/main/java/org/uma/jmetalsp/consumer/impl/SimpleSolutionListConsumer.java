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

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.consumer.AlgorithmDataConsumer;
import org.uma.khaos.perception.core.Observable;
import org.uma.khaos.perception.core.ObservableData;
import org.uma.khaos.perception.core.ObserverListener;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleSolutionListConsumer implements AlgorithmDataConsumer {
  private DynamicAlgorithm<?, ?> dynamicAlgorithm ;

  @Override
  public void setAlgorithm(DynamicAlgorithm<?,?> algorithm) {
    this.dynamicAlgorithm = algorithm ;
  }

  @Override
  public DynamicAlgorithm<?,?> getAlgorithm() {
    return dynamicAlgorithm ;
  }

  @Override
  public void run() {
    if (dynamicAlgorithm == null) {
      throw new JMetalException("The algorithm is null") ;
    }

    Observable observable = dynamicAlgorithm.getObservable() ;

    if (observable == null) {
      throw new JMetalException("Error capturing observable") ;
    }

	  ObservableData<List<? extends Solution<?>>> listOfSolutions ;
    listOfSolutions = (ObservableData<List<? extends Solution<?>>>) observable.getObservableData("currentPopulation");
    listOfSolutions.register(new SolutionListListener<? extends Solution<?>>());

	  while(true){
      try {
        Thread.sleep(1000000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

	private static class SolutionListListener<S extends Solution<?>>
      implements ObserverListener<List<S>> {
    private int counter = 0 ;

		@Override
		public synchronized void update(ObservableData<?> observable, List<S> solutionList) {
			if ((counter % 1 == 0)) {
				JMetalLogger.logger.info("Front number: " + counter+ ". Number of solutions: " + solutionList.size()); ;
			}
			counter ++ ;
		}
	}
}
