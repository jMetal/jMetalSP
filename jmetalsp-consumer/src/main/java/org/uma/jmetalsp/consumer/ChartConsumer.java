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

import org.knowm.xchart.style.Styler;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.extremevalues.impl.FrontExtremeValues;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.referencePoint.impl.NadirPoint;
import org.uma.jmetalsp.AlgorithmDataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData2;
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
        AlgorithmDataConsumer<AlgorithmObservedData2, DynamicAlgorithm<?, AlgorithmObservedData2, Observable<AlgorithmObservedData2>>> {
  private DynamicAlgorithm<?, AlgorithmObservedData2, Observable<AlgorithmObservedData2>> dynamicAlgorithm;

  private ChartContainer chart ;
  List<DoubleSolution> lastReceivedFront = null ;

  public ChartConsumer(DynamicAlgorithm<?, AlgorithmObservedData2, Observable<AlgorithmObservedData2>> algorithm) {
    this.dynamicAlgorithm = algorithm ;
    this.chart = null ;
  }

  @Override
  public DynamicAlgorithm<?, AlgorithmObservedData2, Observable<AlgorithmObservedData2>> getAlgorithm() {
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
    System.out.println("Number of generated fronts: " + data.getAlgorithmData().get("numberOfIterations"));
    if (chart == null) {
      this.chart = new ChartContainer(dynamicAlgorithm.getName(), 200);
      try {
        this.chart.setFrontChart(0, 1, null);
        this.chart.getFrontChart().getStyler().setLegendPosition(Styler.LegendPosition.InsideNE) ;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      this.chart.initChart();
    } else {
      if (data.getSolutionList().size() != 0) {
        if (lastReceivedFront == null) {
          lastReceivedFront = (List<DoubleSolution>) data.getSolutionList();
        } else {
          InvertedGenerationalDistance<DoubleSolution> igd =
                  new InvertedGenerationalDistance<DoubleSolution>(new ArrayFront(lastReceivedFront));
          System.out.println("IGD: " + igd.evaluate((List<DoubleSolution>) data.getSolutionList())) ;
        }
        List<Integer> iteraciones=(List<Integer> )data.getAlgorithmData().get("numberOfIterations");
        this.chart.getFrontChart().setTitle("Iteration: " + iteraciones.get(0));
        //String nameAnt="Front." + iteraciones.get(0);
        this.chart.updateFrontCharts((List<DoubleSolution>) data.getSolutionList(), iteraciones.get(0));
        if(data.getAlgorithmData().get("referencePoints")!=null){
          this.chart.setReferencePoint((List<Double>)data.getAlgorithmData().get("referencePoints"));
        }
        this.chart.refreshCharts();
      }
    }
  }
}
