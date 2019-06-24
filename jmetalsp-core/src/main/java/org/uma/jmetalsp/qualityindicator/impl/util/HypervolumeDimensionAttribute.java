package org.uma.jmetalsp.qualityindicator.impl.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

public class HypervolumeDimensionAttribute <S extends Solution<?>>
    extends GenericSolutionAttribute<S, Double> {

  private int dimension;
}
