package org.uma.jmetalsp.qualityindicator.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.GDominanceComparator;
import org.uma.jmetalsp.qualityindicator.impl.util.NodeHV;

/**
 *
 * Hypervolume computation based on variant 3 of the algorithm in the paper:
 *     C. M. Fonseca, L. Paquete, and M. Lopez-Ibanez. An improved dimension-sweep
 *       algorithm for the hypervolume indicator. In IEEE Congress on Evolutionary
 *       Computation, pages 1157-1163, Vancouver, Canada, July 2006.
 */
public class FonsecaHypervolumen<S extends Solution<?>> extends GenericIndicator<S>{


  private LinkedList<NodeHV> list;
  private List<Double> referencePoints;

 //lo que llama weakly-dominance es nuestro GDominanceComparator

  public FonsecaHypervolumen(List<Double> referencePoints){
    this.list = new LinkedList<>();
    this.referencePoints = referencePoints;
    //this.comparator = new GDominanceComparator<>(this.referencePoints);
  }
  @Override
  public Double evaluate(List<S> solutionList) {
    List<S>  relevantPoints = new ArrayList<>();
    int dimensions = referencePoints.size();
    for (S solution:solutionList) {
      //only consider solutions that dominate the reference point
      if(weaklyDominate(solution)){
        relevantPoints.add(solution);
      }
    }
    preProcess(relevantPoints);
    return null;
  }
  private void preProcess(List<S> solutionList){
    for (Solution s:solutionList) {
      NodeHV<S> node = new NodeHV(s);
      this.list.add(node);
    }

  }
  private boolean weaklyDominate(S solution) {
    boolean result = true ;
    for (int i = 0; i < solution.getNumberOfObjectives() && result; i++) {
      if (solution.getObjective(i) > referencePoints.get(i)) {
        result = false ;
      }
    }
    return result ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return false;
  }
}
