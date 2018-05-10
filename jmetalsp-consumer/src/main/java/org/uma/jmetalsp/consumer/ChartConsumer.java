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

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.style.Styler;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Plots a chart with the produce fronts
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ChartConsumer<S extends Solution<?>> implements
        DataConsumer<AlgorithmObservedData<S>> {

  private DynamicAlgorithm<?, AlgorithmObservedData<S>> dynamicAlgorithm;
  private ChartContainer chart ;
  List<S> lastReceivedFront = null ;

  public ChartConsumer(DynamicAlgorithm<?, AlgorithmObservedData<S>> algorithm) {
    this.dynamicAlgorithm = algorithm ;
    this.chart = null ;
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
    int numberOfIterations = 0 ;
    List<S> solutionList = null ;
    List<Double> referencePoint = null ;
    if (data.getData().containsKey("numberOfIterations")) {
     numberOfIterations =  (int) data.getData().get("numberOfIterations");
    }
    if (data.getData().containsKey("solutionList")) {
      solutionList = (List<S>) data.getData().get("solutionList");
    }

    if (data.getData().containsKey("referencePoint")) {
      referencePoint = (List<Double>) data.getData().get("referencePoint");
    }

    // TODO: error handling if parameters are not included

   // System.out.println("Number of generated fronts: " + data.getData().get("numberOfIterations"));
    if (chart == null) {
      this.chart = new ChartContainer(dynamicAlgorithm.getName(), 200,this.dynamicAlgorithm.getDynamicProblem().getNumberOfObjectives());
      try {
        this.chart.setFrontChart(0, 1, null);
        this.chart.getFrontChart().getStyler().setLegendPosition(Styler.LegendPosition.InsideNE) ;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      this.chart.initChart();
    } else {
      if (solutionList.size() != 0) {
        double coverageValue = 0;
        this.chart.getFrontChart().setTitle("Iteration: " + numberOfIterations);
        if (lastReceivedFront == null) {
          lastReceivedFront = solutionList ;
          this.chart.updateFrontCharts(solutionList, numberOfIterations);
          this.chart.refreshCharts();
        } else {
          Front referenceFront = new ArrayFront(lastReceivedFront);

          InvertedGenerationalDistance<S> igd =
                  new InvertedGenerationalDistance<S>(referenceFront);

          coverageValue=igd.evaluate(solutionList);
        }

        if (coverageValue>0.005) {
          this.chart.updateFrontCharts(solutionList, numberOfIterations);
          lastReceivedFront=solutionList;
          try {
            this.chart.saveChart(numberOfIterations +".chart", BitmapEncoder.BitmapFormat.PNG);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        this.chart.refreshCharts();
      }
    }
  }
}
