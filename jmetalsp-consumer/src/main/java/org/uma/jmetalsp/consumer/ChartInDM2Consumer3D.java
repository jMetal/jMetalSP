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

import java.util.ArrayList;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedSolution;
import org.uma.jmetalsp.observer.Observable;

import java.io.IOException;
import java.util.List;

/**
 * Plots a chart with the produce fronts
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ChartInDM2Consumer3D<S extends Solution<?>> implements
        DataConsumer<AlgorithmObservedData> {

  //private DynamicAlgorithm<?, AlgorithmObservedData<S>> dynamicAlgorithm;
  private String nameAlgorithm;
  private ChartContainer3D chart;
  private List<PointSolution> lastReceivedFront = null;
  private List<Double> referencePoint;

  public ChartInDM2Consumer3D(String nameAlgorithm,
                              List<Double> referencePoint) {
    //this.dynamicAlgorithm = algorithm;
    this.nameAlgorithm = nameAlgorithm;
    this.chart = null;
    this.referencePoint = referencePoint;
  }

  @Override
  public void run() {
   // if (dynamicAlgorithm == null) {
   //   throw new JMetalException("The algorithm is null");
  //  }

   // dynamicAlgorithm.getObservable().register(this);

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
    int numberOfIterations = 0;
    List<PointSolution> solutionList = null;
    List<Double> newReferencePoint = null;
    if (data.getData().containsKey("numberOfIterations")) {
      numberOfIterations = (int) data.getData().get("numberOfIterations");
    }
    if (data.getData().containsKey("solutionList")) {
      //solutionList = (List<S>) data.getData().get("solutionList");
      solutionList = new ArrayList<>() ;
      List<ObservedSolution> receivedList =  (List<ObservedSolution>)data.getData().get("solutionList") ;
      for (int i = 0 ; i< receivedList.size(); i++) {
        solutionList.add(new PointSolution(receivedList.get(i).getPointSolution()));
      }
    }

    if (data.getData().containsKey("referencePoint")) {
      newReferencePoint = (List<Double>) data.getData().get("referencePoint");
    }

    // TODO: error handling if parameters are not included

    double coverageValue = 0;
    if (chart == null) {
      this.chart = new ChartContainer3D(this.nameAlgorithm, 200);
      chart.addFrontChart(0, 1);
      chart.addFrontChart(0, 2);
      chart.addFrontChart(1, 2);
      //chart.setVarChart(0, 1);
      chart.setReferencePoint(referencePoint);
      // ??? this.chart.getFrontChart().getStyler().setLegendPosition(Styler.LegendPosition.InsideNE) ;

      this.chart.initChart();
    } else {
      if (solutionList.size() != 0) {
        List<XYChart> charts = this.chart.getCharts();
        for (XYChart xychart : charts) {
          xychart.setTitle("Iteration: " + numberOfIterations);

          //if (lastReceivedFront == null) {
           // lastReceivedFront = solutionList;
            this.chart.updateFrontCharts(solutionList, numberOfIterations);
           // this.chart.refreshCharts();
          //} else {
            //Front referenceFront = new ArrayFront(lastReceivedFront);

            //InvertedGenerationalDistance igd =
             //       new InvertedGenerationalDistance<S>(referenceFront);

            //coverageValue = igd.evaluate(solutionList);
          //}

          //if (coverageValue > 0.005) {
            //this.chart.updateFrontCharts(solutionList, numberOfIterations);
            //lastReceivedFront = solutionList;
            try {
              this.chart.saveChart(numberOfIterations + ".chart", BitmapEncoder.BitmapFormat.PNG);
            } catch (IOException e) {
              e.printStackTrace();
            }
          //}

          if (newReferencePoint != null) {
            this.chart.setReferencePoint(newReferencePoint);
          }
          this.chart.refreshCharts();
        }
      } else {
        if (newReferencePoint != null) {
          this.chart.setReferencePoint(newReferencePoint);
          this.chart.refreshCharts();
        }
      }
    }
  }
}

