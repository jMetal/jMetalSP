package org.uma.jmetalsp.application.fda.problem.fda3;

import org.uma.jmetalsp.application.fda.problem.fda2.FDA2;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.ProblemBuilder;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDA3ProblemBuilder implements ProblemBuilder,Serializable {
  private  int numberOfVariables;
  private int numberOfObjectives;
  public FDA3ProblemBuilder(int numberOfVariables, int numberOfObjectives){
    this.numberOfVariables=numberOfVariables;
    this.numberOfObjectives=numberOfObjectives;
  }
  @Override
  public DynamicProblem<?, ?> build() throws IOException {
    FDA3 problem = new FDA3(numberOfVariables,numberOfObjectives);
    return (DynamicProblem<?, ?>)problem;
  }
}
