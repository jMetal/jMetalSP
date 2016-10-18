package org.uma.jmetalsp.application.fda.problem.fda4;


import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.ProblemBuilder;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDA4ProblemBuilder implements ProblemBuilder,Serializable {
  private  int numberOfVariables;
  private int numberOfObjectives;
  public FDA4ProblemBuilder(int numberOfVariables, int numberOfObjectives){
    this.numberOfVariables=numberOfVariables;
    this.numberOfObjectives=numberOfObjectives;
  }
  @Override
  public DynamicProblem<?, ?> build() throws IOException {
    FDA4 problem = new FDA4(numberOfVariables,numberOfObjectives);
    return (DynamicProblem<?, ?>)problem;
  }
}
