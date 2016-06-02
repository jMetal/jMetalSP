package org.uma.jmetalsp.problem;

import java.io.IOException;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface ProblemBuilder<P extends DynamicProblem<?,?>> {
  P build() throws IOException;
}
