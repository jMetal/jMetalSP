package org.uma.jmetalsp.spark.evaluator;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.List;

/**
 * Solution list evaluator based on Spark
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SparkSolutionListEvaluator<S extends Solution<?>> implements SolutionListEvaluator<S> {
  static JavaSparkContext sparkContext ;

  public SparkSolutionListEvaluator(JavaSparkContext sparkContext) {
    this.sparkContext = sparkContext ;
  }

  @Override public List<S> evaluate(List<S> solutionList, final Problem<S> problem)
      throws JMetalException {
    /*
    List<S> population = new LinkedList<S>();

    for (int i = 0; i < solutionList.size(); i++) {
      population.add(solutionList.get(i));
    }
*/
    JavaRDD<S> solutionsToEvaluate = sparkContext.parallelize(solutionList);

    JavaRDD<S> evaluatedSolutions =
        solutionsToEvaluate.map(solution -> {
          problem.evaluate(solution);
          return solution;
        });

    return evaluatedSolutions.collect() ;
  }

  @Override
  public void shutdown() {
  }
}
