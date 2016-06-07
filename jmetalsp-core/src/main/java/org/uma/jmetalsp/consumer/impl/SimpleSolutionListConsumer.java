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

import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.consumer.AlgorithmDataConsumer;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimpleSolutionListConsumer implements AlgorithmDataConsumer {
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

    MeasureManager measureManager = dynamicAlgorithm.getMeasureManager() ;

    if (measureManager == null) {
      throw new JMetalException("Error capturing measure manager") ;
    }

    BasicMeasure<List<DoubleSolution>> solutionListMeasure =
        (BasicMeasure<List<DoubleSolution>>) measureManager.<List<DoubleSolution>> getPushMeasure("currentPopulation");

    solutionListMeasure.register(new Listener());
    while(true){
      try {
        Thread.sleep(1000000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private static class Listener<S extends Solution<?>>
      implements MeasureListener<List<S>> {
    private int counter = 0 ;

    @Override synchronized public void measureGenerated(List<S> solutions) {
      if ((counter % 1 == 0)) {
        //JMetalLogger.logger.info("Front number: " + counter+ ". Number of solutions: " + solutions.size()); ;
      }
      counter ++ ;
    }
  }
}
