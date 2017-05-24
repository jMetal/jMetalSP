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
import org.uma.jmetal.qualityindicator.impl.SetCoverage;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;
import org.uma.jmetalsp.AlgorithmDataConsumer;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData2;
import org.uma.jmetalsp.observer.Observable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plots a chart with the produce fronts
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ChartInDM2Consumer implements
        AlgorithmDataConsumer<AlgorithmObservedData2, DynamicAlgorithm<?, AlgorithmObservedData2, Observable<AlgorithmObservedData2>>> {
  private DynamicAlgorithm<?, AlgorithmObservedData2, Observable<AlgorithmObservedData2>> dynamicAlgorithm;

  private ChartContainer chart ;
  private List<DoubleSolution> lastReceivedFront = null ;
  private List<Double> referencePoint ;
  private int sizeIni;
  private Map<String,List<DoubleSolution>> historicalFronts;
  private String nameAnt=null;

  public ChartInDM2Consumer(DynamicAlgorithm<?, AlgorithmObservedData2, Observable<AlgorithmObservedData2>> algorithm,
                            List<Double> referencePoint) {
    this.dynamicAlgorithm = algorithm ;
    this.chart = null ;
    this.referencePoint = referencePoint ;
    this.historicalFronts= new HashMap<String,List<DoubleSolution>>();
    this.nameAnt=null;
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
    //System.out.println("Number of generated fronts: " + data.getIterations());
    if (chart == null) {
      this.chart = new ChartContainer(dynamicAlgorithm.getName(), 200);
      try {
        this.chart.setFrontChart(0, 1, null);
        sizeIni= this.chart.getFrontChart().getStyler().getMarkerSize();

        this.chart.setReferencePoint(referencePoint);
        //this.chart.getFrontChart().getStyler().setMarkerSize(15);
        this.chart.getFrontChart().getStyler().setLegendPosition(Styler.LegendPosition.InsideNE) ;


      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      this.chart.initChart();
    } else {
      if (data.getSolutionList().size() != 0) {
       // this.chart.getFrontChart().getStyler().setMarkerSize(5);
        List<Integer> iteraciones=(List<Integer> )data.getAlgorithmData().get("numberOfIterations");
        List<DoubleSolution> solutionList=(List<DoubleSolution>) data.getSolutionList();
        this.chart.getFrontChart().setTitle("Iteration: " + iteraciones.get(0));
        if (lastReceivedFront == null) {
          lastReceivedFront = (List<DoubleSolution>) data.getSolutionList();
        } else {
          List<DoubleSolution> solution = (List<DoubleSolution>)data.getSolutionList();
          Front referenceFront = new ArrayFront(lastReceivedFront);
          Front front = new ArrayFront(solution);

          FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
          referenceFront = frontNormalizer.normalize(referenceFront);
          //lastReceivedFront = (List<DoubleSolution>)frontNormalizer.normalize(lastReceivedFront);
          InvertedGenerationalDistance<PointSolution> igd =
                  new InvertedGenerationalDistance<PointSolution>(referenceFront);

          front = frontNormalizer.normalize(front);
          System.out.println("IGD: " + igd.evaluate(FrontUtils.convertFrontToSolutionList(front)));
        }
        /*
        double coverageValue=0;
        if(lastFront!=null) {

          coverageValue=coverage.evaluate(solutionList,lastFront);
          //System.out.println("Cobertura "+ coverageValue);
        }
        */
        //lastFront=solutionList;
        //if(coverageValue<0.8) {
          this.chart.updateFrontCharts(solutionList, iteraciones.get(0));//nameAnt
        //  nameAnt="Front." + iteraciones.get(0);
          //historicalFronts.put(nameAnt,solutionList);
        //}

        if(data.getAlgorithmData().get("referencePoints")!=null){
          this.chart.setReferencePoint((List<Double>)data.getAlgorithmData().get("referencePoints"));
          data.getAlgorithmData().put("referencePoints",null);
        }
        this.chart.refreshCharts();
      }
    }
  }
}
