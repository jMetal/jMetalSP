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

public class ChartContainer<S extends Solution<?>> {
  private Map<String, XYChart> charts;
  private XYChart frontChart;
  private XYChart varChart;
  private SwingWrapper<XYChart> sw;
  private String name;
  private int delay;
  private int objective1;
  private int objective2;
  private int variable1;
  private int variable2;
  private Map<String, List<Integer>> iterations;
  private Map<String, List<Double>> indicatorValues;
  private String referenceName;
  private List<String> referencesPointsNames;
  private int numberOfObjectives;
  private List<Integer> indices;
  public ChartContainer(String name,int numObj) {
    this(name, 0,numObj);
  }

  public ChartContainer(String name, int delay,int numberOfObjectives) {
    this.name = name;
    this.delay = delay;
    this.charts = new LinkedHashMap<String, XYChart>();
    this.iterations = new HashMap<String, List<Integer>>();
    this.indicatorValues = new HashMap<String, List<Double>>();
    this.referenceName = null;
    this.referencesPointsNames = new ArrayList<>();
    this.numberOfObjectives = numberOfObjectives;
    this.indices = new ArrayList<>();
  }

  public void setFrontChart(int objective1, int objective2) throws FileNotFoundException {
    this.setFrontChart(objective1, objective2, null);
  }

  public void setFrontChart(int objective1, int objective2, String referenceFrontFileName) throws FileNotFoundException {
    try {
      this.objective1 = objective1;
      this.objective2 = objective2;
      this.frontChart = new XYChartBuilder().xAxisTitle("Objective " + this.objective1)
              .yAxisTitle("Objective " + this.objective2).build();
      this.frontChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).setMarkerSize(5);
      //changeColorFrontChart();
      if (referenceFrontFileName != null) {
        this.displayReferenceFront(referenceFrontFileName);
      }

     // double[] xData = new double[]{1};
    //  double[] yData = new double[]{1};
    //  XYSeries frontChartSeries = this.frontChart.addSeries(this.name, xData, yData);
    //  frontChartSeries.setMarkerColor(Color.blue);

      this.charts.put("Front", this.frontChart);
    }catch (Exception e){

    }
  }

  public void setFrontChart(int objective1, int objective2, String referenceFrontFileName,double ini1, double ini2) throws FileNotFoundException {
    try {
      this.objective1 = objective1;
      this.objective2 = objective2;
      this.frontChart = new XYChartBuilder().xAxisTitle("Objective " + this.objective1)
              .yAxisTitle("Objective " + this.objective2).build();
      this.frontChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).setMarkerSize(5);
      //changeColorFrontChart();
      if (referenceFrontFileName != null) {
        this.displayReferenceFront(referenceFrontFileName);
      }

     // double[] xData = new double[]{ini1};
    //  double[] yData = new double[]{ini2};
     // XYSeries frontChartSeries = this.frontChart.addSeries(this.name, xData, yData);
    //  frontChartSeries.setMarkerColor(Color.blue);

      this.charts.put("Front", this.frontChart);
    }catch (Exception e){

    }
  }

  public void setReferencePoint(List<Double> referencePoint) {
    try {
     // int indexPoint=0;

      for (String name : referencesPointsNames) {
        this.frontChart.removeSeries(name);
      }
      referencesPointsNames = new ArrayList<>();
      this.changeColorFrontChart(Color.GRAY);
      int indexPoint = 0;
      while ( indexPoint < referencePoint.size()) {

        List<Double> auxInterestPoint = nextInterestPoint(indexPoint,numberOfObjectives,referencePoint);

        int j=0;
        String name="";
        while(auxInterestPoint!=null && j<auxInterestPoint.size()-1) {
          name= "Reference Point [" + auxInterestPoint.get(j) + ", " + auxInterestPoint.get(j+1) + "]";

              referencesPointsNames.add(name);
          XYSeries referencePointSeries = this.frontChart.addSeries(
              name,
              new double[]{auxInterestPoint.get(j)},
              new double[]{auxInterestPoint.get(j+1)});
          referencePointSeries.setMarkerColor(Color.green);
          j+=2;
        }
        indexPoint+=numberOfObjectives;

      }
      orderAllFront();

    }catch (Exception e){
//e.printStackTrace();
    }
  }
  private List<Double> nextInterestPoint(int index, int size,List<Double> interestPoint){
    List<Double> result= null;
    if(index<interestPoint.size()){
      result = new ArrayList<>(size);
      for(int i=0;i<size;i++){
        result.add(interestPoint.get(index));
        index++;
      }
    }
    return  result;
  }
  /*public void setReferencePoint(List<Double> referencePoint) {
    try {
      double rp1 = referencePoint.get(this.objective1);
      double rp2 = referencePoint.get(this.objective2);
      if (referenceName != null) {
        //this.changeColorFrontChart(Color.GRAY);
        this.frontChart.removeSeries(referenceName);
      }
      referenceName = "Reference Point [" + rp1 + ", " + rp2 + "]";

      XYSeries referencePointSeries = this.frontChart.addSeries(referenceName,
              new double[]{rp1},
              new double[]{rp2});
      referencePointSeries.setShowInLegend(true);
      orderAllFront();

      // referencePointSeries.setMarkerColor(Color.green);
    }catch(Exception e){

    }

  }*/

  private void deleteAllFront() {
    if (this.frontChart != null && this.frontChart.getSeriesMap() != null) {
      Set<String> keys = this.frontChart.getSeriesMap().keySet();
      if (keys != null) {
        Object[] obj = keys.toArray();
        if (obj != null) {
          String[] listFront = new String[obj.length];
          for (int i = 0; i < obj.length; i++) {
            listFront[i] = obj[i].toString();
          }
          for (int i = 0; i < listFront.length; i++) {
            String name = listFront[i];
            if (name != this.name && !name.contains("Reference") && this.frontChart.getSeriesMap().get(name).getMarkerColor() != Color.GRAY) {
              this.frontChart.removeSeries(name);
            }
          }
        }
      }
    }
  }

  private void orderAllFront() {
    try{
    if (this.frontChart != null && this.frontChart.getSeriesMap() != null) {
      Set<String> keys = this.frontChart.getSeriesMap().keySet();
      if (keys != null) {
        Object[] obj = keys.toArray();
        if (obj != null) {
          String[] listFront = new String[obj.length];
          for (int i = 0; i < obj.length; i++) {
            listFront[i] = obj[i].toString();
          }
          for (int i = 0; i < listFront.length; i++) {
            String name = listFront[i];
            if (name != this.name && !name.contains("Reference")) {
              XYSeries xy = this.frontChart.getSeriesMap().get(name);
              this.frontChart.removeSeries(name);

              this.frontChart.addSeries(name, generateArray(xy.getXData()), generateArray(xy.getYData()));
            }
          }
          this.changeColorFrontChart(Color.lightGray);
        }
      }
    }
    }catch (Exception e){

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

  public void setVarChart(int variable1, int variable2) {
    this.variable1 = variable1;
    this.variable2 = variable2;
    this.varChart = new XYChartBuilder().xAxisTitle("Variable " + this.variable1)
            .yAxisTitle("Variable " + this.variable2).build();

    this.varChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).setMarkerSize(5);

    double[] xData = new double[]{0};
    double[] yData = new double[]{0};

    XYSeries varChartSeries = this.varChart.addSeries(this.name, xData, yData);
    varChartSeries.setMarkerColor(Color.blue);

    this.charts.put("VAR", this.varChart);
  }

  public void initChart() {
    try {
      this.sw = new SwingWrapper<XYChart>(new ArrayList<XYChart>(this.charts.values()));
      this.sw.displayChartMatrix(this.name);
    }catch(Exception e){

    }
  }

  public void updateFrontCharts(List<S> solutionList, int counter) {
    if (this.frontChart != null) {
      if (this.frontChart.getSeriesMap() != null) {
        // this.deleteAllFront();//delete the other fronts
      }
      if (!indices.contains(counter)) {


        this.frontChart.addSeries("Front." + counter,
                this.getSolutionsForObjective(solutionList, this.objective1),
                this.getSolutionsForObjective(solutionList, this.objective2),
                null);
        indices.add(counter);
      }

    }
  }

  private void changeColorFrontChart(Color color) {
    if (this.frontChart.getSeriesMap() != null) {
      Set<String> keys = this.frontChart.getSeriesMap().keySet();
      Iterator<String> it = keys.iterator();
      while (it.hasNext()) {
        String name = it.next();
        if (!name.contains("Reference") && name != this.name) {
          this.frontChart.getSeriesMap().get(name).setMarkerColor(color);
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

  public void addIndicatorChart(String indicator) {
    XYChart indicatorChart = new XYChartBuilder().xAxisTitle("n").yAxisTitle(indicator).build();
    indicatorChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).setMarkerSize(5);

    List<Integer> indicatorIterations = new ArrayList<Integer>();
    indicatorIterations.add(0);
    List<Double> indicatorValues = new ArrayList<Double>();
    indicatorValues.add(0.0);

    XYSeries indicatorSeries = indicatorChart.addSeries(this.name, indicatorIterations, indicatorValues);
    indicatorSeries.setMarkerColor(Color.blue);

    this.iterations.put(indicator, indicatorIterations);
    this.indicatorValues.put(indicator, indicatorValues);
    this.charts.put(indicator, indicatorChart);
  }

  public void removeIndicator(String indicator) {
    this.iterations.remove(indicator);
    this.indicatorValues.remove(indicator);
    this.charts.remove(indicator);
  }

  public void updateIndicatorChart(String indicator, Double value) {
    this.indicatorValues.get(indicator).add(value);
    this.iterations.get(indicator).add(this.indicatorValues.get(indicator).size());

    this.charts.get(indicator).updateXYSeries(this.name, this.iterations.get(indicator),
            this.indicatorValues.get(indicator), null);
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

  private void displayFront(String name, String fileName, int objective1, int objective2)
          throws FileNotFoundException {
    ArrayFront front = new ArrayFront(fileName);
    double[][] data = FrontUtils.convertFrontToArray(front);
    double[] xData = getObjectiveValues(data, objective1);
    double[] yData = getObjectiveValues(data, objective2);
    XYSeries referenceFront = this.frontChart.addSeries(name, xData, yData);
    referenceFront.setMarkerColor(Color.red);
  }

  private void displayReferenceFront(String fileName) throws FileNotFoundException {
    this.displayReferenceFront(fileName, this.objective1, this.objective2);
  }

  private void displayReferenceFront(String fileName, int objective1, int objective2) throws FileNotFoundException {
    this.displayFront("Reference Front", fileName, objective1, objective2);
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

  public ChartContainer setName(String name) {
    this.name = name;
    return this;
  }

  public int getDelay() {
    return this.delay;
  }

  public ChartContainer setDelay(int delay) {
    this.delay = delay;
    return this;
  }

  public XYChart getFrontChart() {
    return this.frontChart;
  }

  public XYChart getVarChart() {
    return this.varChart;
  }

  public XYChart getChart(String chartName) {
    return this.charts.get(chartName);
  }
}
