package org.uma.jmetalsp.algorithm;


/**
 * Integration test for class DynamicMOCell
 *
 * @author Antonio J. Nebro <ajnebro@uma.es>
 */
public class DynamicMOCellTest {
/*
  @Test
  public void shouldConstructorCreateTheAlgorithm() {
    DynamicProblem<DoubleSolution,MockedUpdateData> problem = new MockedDynamicProblem<DoubleSolution, MockedUpdateData>() ;
    DynamicMOCell<DoubleSolution> dynamicMOCell = new DynamicMOCell<DoubleSolution>(
            problem,
            25000,
            100,
            new CrowdingDistanceArchive<DoubleSolution>(100),
            new C9<DoubleSolution>(10, 10),
            new SBXCrossover(0.9, 20.0),
            new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0),
            new BinaryTournamentSelection<DoubleSolution>(),
            new SequentialSolutionListEvaluator<DoubleSolution>()
            ) ;

    assertNotNull(dynamicMOCell) ;
    assertSame(problem, dynamicMOCell.getProblem()) ;
  }

  @Test
  public void shouldRunningTheAlgorithmForOneIterationCreateAFUNAndAVARFile() {
    DynamicProblem<DoubleSolution,MockedUpdateData> problem = new MockedDynamicProblem<DoubleSolution, MockedUpdateData>() ;
    DynamicMOCell<DoubleSolution,?> dynamicMOCell = new DynamicMOCell<DoubleSolution,?>(
            problem,
            100,
            100,
            new CrowdingDistanceArchive<DoubleSolution>(100),
            new C9<DoubleSolution>(10, 10),
            new SBXCrossover(0.9, 20.0),
            new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0),
            new BinaryTournamentSelection<DoubleSolution>(),
            new SequentialSolutionListEvaluator<DoubleSolution>()
    ) ;

    assertNotNull(dynamicMOCell) ;
    assertSame(problem, dynamicMOCell.getProblem()) ;
    assertEquals(0, dynamicMOCell.getCompletedIterations()) ;
  }

  private class MockedDynamicProblem<S extends Solution<?>, D extends ObservedData> implements DynamicProblem<S, D> {

    @Override
    public boolean hasTheProblemBeenModified() {
      return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(D data) {

    }

    @Override
    public int getNumberOfVariables() {
      return 10;
    }

    @Override
    public int getNumberOfObjectives() {
      return 0;
    }

    @Override
    public int getNumberOfConstraints() {
      return 0;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public void evaluate(S solution) {

    }

    @Override
    public S createSolution() {
      return null;
    }
  }

  private class MockedUpdateData implements ObservedData {
  }
  */
}