package org.uma.jmetalsp.algorithm;

import org.uma.jmetalsp.problem.DynamicProblem;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface AlgorithmBuilder<
    A extends DynamicAlgorithm<?>,
    P extends DynamicProblem<?, ?>> {

  A build(P problem) ;
}
