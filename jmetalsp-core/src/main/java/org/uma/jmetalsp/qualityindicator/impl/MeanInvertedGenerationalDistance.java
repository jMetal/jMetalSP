package org.uma.jmetalsp.qualityindicator.impl;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;

public class MeanInvertedGenerationalDistance<S extends Solution<?>> extends
    GenericIndicator<List<S>> {

  private InvertedGenerationalDistance igd;

  public MeanInvertedGenerationalDistance() {
    igd = new InvertedGenerationalDistance();
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public MeanInvertedGenerationalDistance(String referenceParetoFrontFile, double p) throws FileNotFoundException {
    igd = new InvertedGenerationalDistance(referenceParetoFrontFile,p) ;

  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public MeanInvertedGenerationalDistance(String referenceParetoFrontFile) throws FileNotFoundException {
    igd = new InvertedGenerationalDistance(referenceParetoFrontFile) ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public MeanInvertedGenerationalDistance(Front referenceParetoFront) {
    igd = new InvertedGenerationalDistance(referenceParetoFront) ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return igd.isTheLowerTheIndicatorValueTheBetter();
  }


  @Override
  public Double evaluate(List<List<S>> lists) {
    double mean = 0.0d;
    int size = lists.size();
    for (List<S> front:lists) {
      mean+=igd.evaluate(front);
    }
    mean = mean/size;
    return mean;
  }
}
