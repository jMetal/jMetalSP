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

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.extremevalues.impl.FrontExtremeValues;
import org.uma.jmetal.util.referencePoint.impl.NadirPoint;
import org.uma.jmetalsp.AlgorithmDataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plots a chart with the produce fronts
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ChartConsumer implements
        AlgorithmDataConsumer<AlgorithmObservedData, DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>>> {
  private DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>> dynamicAlgorithm;

  private ChartContainer chart ;
  List<DoubleSolution> lastReceivedFront = new ArrayList<>() ;

  public ChartConsumer(DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>> algorithm) {
    this.dynamicAlgorithm = algorithm ;
    this.chart = null ;
  }

  @Override
  public DynamicAlgorithm<?, AlgorithmObservedData, Observable<AlgorithmObservedData>> getAlgorithm() {
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
  public void update(Observable<AlgorithmObservedData> observable, AlgorithmObservedData data) {
    System.out.println("Number of generated fronts: " + data.getIterations());
    if (chart == null) {
      this.chart = new ChartContainer(dynamicAlgorithm.getName(), 200);
      try {
        this.chart.setFrontChart(0, 1, null);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      this.chart.initChart();
    } else {
      if (!lastReceivedFront.equals((List<DoubleSolution>) data.getSolutionList())) {
        this.chart.getFrontChart().setTitle("Iteration: " + data.getIterations());
        this.chart.updateFrontCharts((List<DoubleSolution>) data.getSolutionList(), data.getIterations());
        this.chart.refreshCharts();
      }
    }
  }
}
