package org.uma.jmetalsp.spark.util;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 26/06/14.
 */
public class SparkSolutionListEvaluator<S extends Solution<?>> implements SolutionListEvaluator<S>, Serializable {
  static JavaSparkContext sparkContext ; // = new JavaSparkContext(sparkConf);

  public SparkSolutionListEvaluator(JavaSparkContext sparkContext) {
    this.sparkContext = sparkContext ;
  }

  @Override public List<S> evaluate(List<S> solutionSet, final Problem<S> problem)
      throws JMetalException {
    List<S> population = new LinkedList<S>();

    for (int i = 0; i < solutionSet.size(); i++) {
      population.add(solutionSet.get(i));
    }

    JavaRDD<S> solutionsToEvaluate = sparkContext.parallelize(population);

    JavaRDD<S> evaluatedSolutions =
        solutionsToEvaluate.map(new Function<S, S>() {
          @Override public S call(S solution) throws Exception {
            S sol = (S)solution.copy() ;
            problem.evaluate(sol);
            return sol;
          }
        });

    List<S> newPopulation = evaluatedSolutions.collect();

    List<S> resultSolutionSet = new ArrayList<>(solutionSet.size()) ;
    for (Solution solution : newPopulation) {
      resultSolutionSet.add((S)solution) ;
    }

    return resultSolutionSet;
  }

  @Override
  public void shutdown() {
  }
}
