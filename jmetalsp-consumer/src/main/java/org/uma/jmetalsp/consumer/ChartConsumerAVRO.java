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
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetalsp.DataConsumer;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.ObservedSolution;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.KafkaBasedConsumer;
import org.uma.jmetalsp.serialization.algorithmdata.AlgorithmData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plots a chart with the produce fronts
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ChartConsumerAVRO<S extends Solution<?>> implements
        DataConsumer<AlgorithmData> {


  private String algorithmName;
  private ChartContainer chart ;
  List<PointSolution> lastReceivedFront = null ;


  public ChartConsumerAVRO() {
    algorithmName = "";
    this.chart = null ;
  }
  public ChartConsumerAVRO(String algorithmName) {
    this.chart = null ;
    this.algorithmName = algorithmName;
  }

  @Override
  public void run() {

    while (true) {
      try {
        Thread.sleep(1000000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void update(Observable<AlgorithmData> observable, AlgorithmData data) {
    int numberOfIterations = 0 ;
    List<PointSolution> solutionList = null ;
    List<Double> referencePoint = null ;

     numberOfIterations =  data.getNumberOfIterations();

    if (data.getObjectives()!=null) {
      solutionList =  generatePoints(data.getObjectives());
    }

    if (data.getReferencePoints()!=null) {
      referencePoint = data.getReferencePoints();
    }

    // TODO: error handling if parameters are not included

    if (chart == null) {
      this.chart = new ChartContainer(
          data.getAlgorithmName(),
          200,
          data.getNumberOfObjectives());
      try {
        double ini1=0;
        double ini2=0;
        if (data.getObjectives()!=null && this.chart.getName().toUpperCase().contains("TSP")) {
          List<List<Double>> receivedList = data.getObjectives();
          if(receivedList!=null && !receivedList.isEmpty()){
            ini1= (double)receivedList.get(0).get(0);
            ini2= (double)receivedList.get(0).get(1);
          }
        }
        this.chart.setFrontChart(0, 1, null,ini1,ini2);
        this.chart.getFrontChart().getStyler().setLegendPosition(Styler.LegendPosition.InsideNE) ;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      this.chart.initChart();
    } else {
      if (solutionList.size() != 0) {
       // double coverageValue = 0;
        this.chart.getFrontChart().setTitle(algorithmName+" Iteration: " + numberOfIterations);
        //if (lastReceivedFront == null) {
          lastReceivedFront = solutionList;
          this.chart.updateFrontCharts(solutionList, numberOfIterations);
          //this.chart.refreshCharts();
        //} else {
          //Front referenceFront = new ArrayFront(lastReceivedFront);

          //InvertedGenerationalDistance<PointSolution> igd =
           //       new InvertedGenerationalDistance<PointSolution>(referenceFront);

          //coverageValue=igd.evaluate(solutionList);
       // }

        //if (coverageValue>0.005) {
          //this.chart.updateFrontCharts(solutionList, numberOfIterations);
          //lastReceivedFront=solutionList;
          try {
            this.chart.saveChart(numberOfIterations +".chart", BitmapEncoder.BitmapFormat.PNG);
          } catch (IOException e) {
            e.printStackTrace();
          }
        //}
        this.chart.refreshCharts();
      }
    }
  }

  private List<PointSolution> generatePoints(List<List<Double>> list){
    List<PointSolution> points= new ArrayList<>();
    for (List<Double> objs: list){
      PointSolution pointSolution = new PointSolution(objs.size());
      for (int i = 0; i < objs.size(); i++) {
        pointSolution.setObjective(i,objs.get(i));
      }
      points.add(pointSolution);
    }
    return points;
  }

  ////////////////////////////////////////////////
  public static void main(String[] args) {
    String topicName = "prueba-solutionlist-topic-from-main";

    ChartConsumerAVRO chartConsumer = new ChartConsumerAVRO() ;

    KafkaBasedConsumer<AlgorithmObservedData> chartKafkaBasedConsumer =
      new KafkaBasedConsumer<>(topicName, chartConsumer, new AlgorithmData()) ;

    chartKafkaBasedConsumer.start();

    chartConsumer.run();
  }
}
