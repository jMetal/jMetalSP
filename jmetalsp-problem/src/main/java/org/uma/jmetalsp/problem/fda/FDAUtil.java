package org.uma.jmetalsp.problem.fda;

import org.uma.jmetalsp.problem.ProblemBuilder;
import org.uma.jmetalsp.problem.fda.fda1.FDA1ProblemBuilder;
import org.uma.jmetalsp.problem.fda.fda2.FDA2ProblemBuilder;
import org.uma.jmetalsp.problem.fda.fda3.FDA3ProblemBuilder;
import org.uma.jmetalsp.problem.fda.fda4.FDA4ProblemBuilder;
import org.uma.jmetalsp.problem.fda.fda5.FDA5ProblemBuilder;

import java.io.Serializable;

/**
 * Created by cris on 09/01/2017.
 */
public class FDAUtil implements Serializable {
  public static ProblemBuilder load(String name){
    ProblemBuilder problemBuilder=null;
    switch (name){
      case "fda1":
        problemBuilder =new FDA1ProblemBuilder(20,2);
        break;
      case "fda2":
        problemBuilder =new FDA2ProblemBuilder(31,2);
        break;
      case "fda3":
        problemBuilder =new FDA3ProblemBuilder(30,2);
        break;
      case "fda4":
        problemBuilder =new FDA4ProblemBuilder(12,3);
        break;
      case "fda5":
        problemBuilder =new FDA5ProblemBuilder(12,3);
        break;
        default:
          problemBuilder =new FDA1ProblemBuilder(20,2);

    }

    return problemBuilder;
  }
}
