package org.uma.jmetalsp.examples.dynamictsp;

/**
 * Example of SparkSP application.
 * Features:
 * - Algorithm: InDM2
 * - Problem: Bi-objective TSP (using data files from TSPLIB)
 * - Default streaming runtime (Spark is not used)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InDM2RunnerForTSP {
/*
  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            MatrixObservedData<Double>,
            AlgorithmObservedData,
            DynamicProblem<DoubleSolution, MatrixObservedData<Double>>,
            DynamicAlgorithm<List<PermutationSolution<Integer>>, Observable<AlgorithmObservedData>>,
            StreamingTSPSource,
            AlgorithmDataConsumer<AlgorithmObservedData, DynamicAlgorithm<List<PermutationSolution<Integer>>, Observable<AlgorithmObservedData>>>> application;
    application = new JMetalSPApplication<>();

    // Set the streaming data source for the problem
    Observable<MatrixObservedData<Double>> streamingTSPDataObservable =
            new DefaultObservable<>("streamingTSPObservable");
    StreamingDataSource<?, ?> streamingDataSource = new StreamingTSPSource(streamingTSPDataObservable, 20000);

    // Set the streaming data source for the algorithm
    Observable<ListObservedData<Double>> algorithmObservable = new DefaultObservable<>("Algorithm observable");
    StreamingDataSource<ListObservedData<Double>, Observable<ListObservedData<Double>>> streamingDataSource2 =
            new SimpleStreamingDataSourceFromKeyboard(algorithmObservable);

    // Problem configuration
    DynamicProblem<PermutationSolution<Integer>, MatrixObservedData<Double>> problem;
    problem = new MultiobjectiveTSPBuilderFromTSPLIBFiles("data/kroA100.tsp", "data/kroB100.tsp")
            .build(streamingTSPDataObservable);
    //problem = new MultiobjectiveTSPBuilderFromNY("initialDataFile.txt")
    //        .build(streamingTSPDataObservable);
    //System.out.println(problem);

    // Algorithm configuration
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

    crossover = new PMXCrossover(0.9);

    double mutationProbability = 0.2;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability);

    selection = new BinaryTournamentSelection<>(
            new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    InDM2<PermutationSolution<Integer>> algorithm;
    Observable<AlgorithmObservedData<PermutationSolution<Integer>>> observable = new DefaultObservable<>("InDM2");

    List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(180000.0);
    referencePoint.add(60000.0);

    int populationSize = 50;
    algorithm = new InDM2Builder<
            PermutationSolution<Integer>,
            DynamicProblem<PermutationSolution<Integer>, ?>,
            Observable<AlgorithmObservedData<PermutationSolution<Integer>>>>(crossover, mutation, referencePoint, observable)
            .setMaxIterations(400000)
            .setPopulationSize(populationSize)
            .build(problem);

    algorithm.setRestartStrategyForProblemChange(new RestartStrategy<PermutationSolution<Integer>>(
            new RemoveNRandomSolutions<>(0),
            //new RemoveNSolutionsAccordingToTheHypervolumeContribution<PermutationSolution<Integer>>(50),
            new CreateNRandomSolutions<PermutationSolution<Integer>>(0)));

    algorithm.setRestartStrategyForReferencePointChange(new RestartStrategy<PermutationSolution<Integer>>(
            new RemoveNRandomSolutions<>(80),
            new CreateNRandomSolutions<>(20)));

    algorithmObservable.register(algorithm);

    application.setStreamingRuntime(new DefaultRuntime<SingleObservedData<Integer>,
            Observable<SingleObservedData<Integer>>,
            SimpleStreamingCounterDataSource>())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource)
            .addStreamingDataSource(streamingDataSource2)
            .addAlgorithmDataConsumer(new ChartInDM2Consumer(algorithm, referencePoint))
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory", algorithm))
            .run();
  }
  */
}
