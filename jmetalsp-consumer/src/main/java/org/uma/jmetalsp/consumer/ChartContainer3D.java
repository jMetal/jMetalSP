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

import org.knowm.xchart.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class for configuring and displaying a XChart.
 *
 * @author Jorge Rodriguez Ordonez
 */

public class ChartContainer3D<S extends Solution<?>> {
  private Map<String, XYChart> charts;
  private SwingWrapper<XYChart> sw;
  private String name;
  private int delay;
  private Map<String, List<Integer>> iterations;
  private String referenceName;

  public ChartContainer3D(String name) {
    this(name, 0);
  }

  public ChartContainer3D(String name, int delay) {
    this.name = name;
    this.delay = delay;
    this.charts = new LinkedHashMap<String, XYChart>();
    this.iterations = new HashMap<String, List<Integer>>();
    this.referenceName = null;
  }

  public void addFrontChart(int objective1, int objective2) {
    XYChart chart = new XYChartBuilder()
            .xAxisTitle("Objective " + objective1)
            .yAxisTitle("Objective " + objective2)
            .build();
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).setMarkerSize(5);

    double[] xData = new double[]{0};
    double[] yData = new double[]{0};
    XYSeries frontChartSeries = chart.addSeries(this.name, xData, yData);
    frontChartSeries.setMarkerColor(Color.blue);

    this.charts.put("" + objective1 + "," + objective2, chart) ;
  }

  public void setReferencePoint(List<Double> referencePoint) {
    for (int i = 0; i < referencePoint.size(); i++) {
      for (int j = i +1 ; j < referencePoint.size(); j++) {
        String key = "" + i + "," + j ;
        System.out.println(key) ;
        XYSeries referencePointSeries = charts.get(key).addSeries("Reference Point [" + referencePoint.get(i) + ", " + referencePoint.get(j) + "]",
                new double[]{referencePoint.get(i)},
                new double[]{referencePoint.get(j)});
        referencePointSeries.setMarkerColor(Color.green);
      }
    }
  }

  private double[] generateArray(Collection collection) {
    double[] result = null;
    if (collection != null) {
      result = new double[collection.size()];
      Object[] aux = collection.toArray();
      for (int i = 0; i < aux.length; i++) {
        result[i] = Double.parseDouble(aux[i].toString());
      }
    }
    return result;
  }

  public void initChart() {
    try {
      this.sw = new SwingWrapper<XYChart>(new ArrayList<XYChart>(this.charts.values()));
      this.sw.displayChartMatrix(this.name);
    }catch(Exception e){

    }
  }

  public void updateFrontCharts(List<S> solutionList, int counter) {
    for (Map.Entry<String, XYChart> entry : this.charts.entrySet()) {

      int objective1 = Integer.parseInt(entry.getKey().substring(0, 1)) ;
      int objective2 = Integer.parseInt(entry.getKey().substring(2, 3)) ;

      entry.getValue().updateXYSeries(this.name,
              this.getSolutionsForObjective(solutionList, objective1),
              this.getSolutionsForObjective(solutionList, objective2), null) ;
    }
  }

  private void changeColorFrontChart(Color color) {
    for (Map.Entry<String, XYChart> entry : this.charts.entrySet()) {
      if (entry.getValue().getSeriesMap() != null) {
        Set<String> keys = entry.getValue().getSeriesMap().keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
          String name = it.next();
          if (!name.contains("Reference") && name != this.name) {
            entry.getValue().getSeriesMap().get(name).setMarkerColor(color);
          }
        }
      }
    }
  }


  public void refreshCharts() {
    this.refreshCharts(this.delay);
  }

  public void refreshCharts(int delay) {
    if (delay > 0) {
      try {
        TimeUnit.MILLISECONDS.sleep(delay);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    this.repaint();
  }

  public void repaint() {
    try {
      for (int i = 0; i < this.charts.values().size(); i++) {
        //System.out.println("Size: " + charts.values().size()) ;
        this.sw.repaintChart(i);
      }
    } catch (IndexOutOfBoundsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private double[] getObjectiveValues(double[][] data, int obj) {
    double[] values = new double[data.length];
    for (int i = 0; i < data.length; i++) {
      values[i] = data[i][obj];
    }
    return values;
  }

  private double[] getSolutionsForObjective(List<S> solutionList, int objective) {
    double[] result = new double[solutionList.size()];
    for (int i = 0; i < solutionList.size(); i++) {
      result[i] = solutionList.get(i).getObjective(objective);
    }
    return result;
  }


  public void saveChart(String fileName, BitmapEncoder.BitmapFormat format) throws IOException {
    for (String chart : this.charts.keySet()) {
      BitmapEncoder.saveBitmap(this.charts.get(chart), fileName + "_" + chart, format);
    }
  }

  public String getName() {
    return this.name;
  }

  public ChartContainer3D setName(String name) {
    this.name = name;
    return this;
  }

  public int getDelay() {
    return this.delay;
  }

  public ChartContainer3D setDelay(int delay) {
    this.delay = delay;
    return this;
  }

  public List<XYChart> getCharts() {
    List<XYChart> chartList = new ArrayList<>() ;

    for (Map.Entry<String, XYChart> entry : this.charts.entrySet()) {
      chartList.add(entry.getValue()) ;
    }
    return chartList ;
  }
}
