package  org.uma.jmetalsp.application.fda.problem;

import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.ProblemBuilder;

import java.io.IOException;

/**
 * Created by cris on 20/07/2016.
 */
public class FDA1ProblemBuilder implements ProblemBuilder {
  private  int numberOfVariables;
  private int numberOfObjectives;
  public FDA1ProblemBuilder(int numberOfVariables, int numberOfObjectives){
    this.numberOfVariables=numberOfVariables;
    this.numberOfObjectives=numberOfObjectives;
  }
  @Override
  public DynamicProblem<?, ?> build() throws IOException {
    FDA1 problem = new FDA1(numberOfVariables,numberOfObjectives);
    return (DynamicProblem<?, ?>)problem;
  }
}
