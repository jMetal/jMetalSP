Dynamic Multi-objective optimization
======================================================

Before reading this section, readers are referred to the papers "jMetalSP: A framework for dynamic multi-objective big data optimization" (DOI:https://doi.org/10.1016/j.asoc.2017.05.004) and "A multi-objective interactive dynamic particle swarm optimizer" (https://link.springer.com/article/10.1007/s13748-019-00198-8) and to the :ref:`installation`. This tutorial is intended as a guide to explain how use and configure dynamic metaheuristcs in jMetalSP. Please, take into account that this is a work in progress, based on the new major version of jMetalSP which is in development. Comments, suggestions, and bugs reporting are welcome.

Motivation
----------
Many optimization problems involve multiple objectives, constraints and parameters that change over time, these change can affect the Pareto set, the Pareto front, or both of them. These problems are called dynamic multiobjective optimization problems (DMOPs).
Solving dynamic problems implies that metaheuristics must be adapted to detect changes in the problems and to react consequently.

When dealing with multi-objective optimization problems, finding an approximation to the Pareto front of a multi-objective problem is only the first step of the optimization process. The second step is MCDM (multi-criteria decision making), and it is related to choosing which solution of that front is most adequate according to the requirements imposed by the decision maker (i.e., the final user, which is an expert in the problem domain). As the optimization can take a significant amount of time, the decision maker may be interested in indicating one or more preference regions of interest of the Pareto front instead of the full front. These regions can be indicated a priori (before running the optimization algorithm) or interactively (during the execution of the algorithm). In jMetal we focus on interactively algorithms.

A current trend in multi-objective optimization is to deal with dynamic optimization problems and furthermore, including MCDM requirements. 
jMetal includes dynamic multi-objective metaheuristics such as: DynamicNSGA-II, DynamicSMPSO and DynamicSMPSO-RP which is also able to take into account user preferences through reference points.


In order to facilitate the visualization of the fronts in dynamic executions and the interactivity with the user for changing references point jMetal contains chart visualizator and keyboard management.

Dynamic Multi-objective Problem
-------------------------------
jMetalSP contains the dynamic multi-objective problem Dynamic TSP and families FDA and DF. The formulation is defined in "Dynamic multiobjective optimization problems: test cases, approximations, and applications" (DOI:https://doi.org/10.1109/TEVC.2004.831456).

Dynamic Multi-objective Algorithm
---------------------------------

In jMetalSP we define an interface for describing the main methods for a dynamic multi-objective algorithm. With the method ``restart()`` we indicate which is the restarting strategy in the dynamic  algorithm. Furthermore, the method ``getObservable()`` returns the ``Observable`` class that implements observer pattern.
Although, when we develop a new dynamic algorithm based on an existed metaheuristics, e.g. DynamicNSGAII from NSGAII, we need to override at least two methods ``isStoppingConditionReached()`` for managing whether the algorithm is endless or not and ``updateProgress()`` method to know if problem parameter has changed. 

.. code-block:: java

 public interface DynamicAlgorithm<Result, O extends ObservedData<?>> extends Algorithm<Result>{
  DynamicProblem<?, ?> getDynamicProblem() ;
  void restart();
  void setRestartStrategy(RestartStrategy<?> restartStrategy);
  Observable<O> getObservable() ;
}

Dynamic Multi-objective Runner
------------------------------
The dynamic multi-objective runners in jMetalSP is similar to no dynamic runners from jMetal, but there exist slight difference as we can see below, for instance ``DynamicContinuousApplication`` class.

.. code-block:: java

 public static void main(String[] args) throws Exception {
    // STEP 1. Create the problem
	  DynamicProblem<DoubleSolution, ObservedValue<Integer>> problem =
            new FDA2();

	  // STEP 2. Create the algorithm
    DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData> algorithm =
            AlgorithmFactory.getAlgorithm("NSGAII", problem) ;

    algorithm.setRestartStrategy(new RestartStrategy<>(
            //new RemoveFirstNSolutions<>(50),
            //new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(50),
            //new RemoveNSolutionsAccordingToTheCrowdingDistance<>(50),
            new RemoveNRandomSolutions<>(15),
            new CreateNRandomSolutions<DoubleSolution>()));

    // STEP 3. Create the streaming data source (only one in this example)
    StreamingDataSource<ObservedValue<Integer>> streamingDataSource =
            new SimpleStreamingCounterDataSource(1000) ;

    // STEP 4. Create the data consumers and register into the algorithm
    DataConsumer<AlgorithmObservedData> localDirectoryOutputConsumer =
            new LocalDirectoryOutputConsumer<DoubleSolution>("outputdirectory") ;
    DataConsumer<AlgorithmObservedData> chartConsumer =
            new ChartConsumer<DoubleSolution>(algorithm.getName()) ;

    // STEP 5. Create the application and run
    JMetalSPApplication<
            DoubleSolution,
            DynamicProblem<DoubleSolution, ObservedValue<Integer>>,
            DynamicAlgorithm<List<DoubleSolution>, AlgorithmObservedData>> application;

    application = new JMetalSPApplication<>();

    application
            .setStreamingRuntime(new DefaultRuntime())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource,problem)
            .addAlgorithmDataConsumer(localDirectoryOutputConsumer)
            .addAlgorithmDataConsumer(chartConsumer)
            .run();
 }


There are a number of items to be considered:

* In step 2 is created the dynamic algorithm, we set up the restart policies.
* In step 3 is defined the counter for updating the problem.
* In step 2.4 we define an important variable from a point of view of the dynamic problem, thus with ``updateProblemByIterations`` we indicate whether the problem is updated following the number of iterations of the algorithm or with a external counter (as we will see in next code example).
* Step 3 and step 4 configure the chart visualizator, The value ``80`` means the display delay in milliseconds.

The next example is ``DynamicSMPSORunnerStreaming`` class where we have set a streaming counter for updating the dynamic problem. It is worth noticing that the value of ``updateProblemByIterations`` in this case is ``false`` because it is the counter who modifies the dynamic problem. In the step 4 is configured the streaming counter which create a new value every ``2000 miliseconds``.

.. code-block:: java

   public static void main(String[] args) throws Exception {
    // STEP 1. Create the problem
    DynamicProblem<DoubleSolution, Integer> problem = new FDA2();

    // STEP 2. Create the algorithm
    DynamicAlgorithm<List<DoubleSolution>>  algorithm;
    // STEP 2.1. Create the operators
    
    MutationOperator<DoubleSolution> mutation;

    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    int swarmSize = 100;
    int maxIterations = 250;

    double r1Max = 1.0;
    double r1Min = 0.0;
    double r2Max = 1.0;
    double r2Min = 0.0;
    double  c1Max = 2.5;
    double  c1Min = 1.5;
    double  c2Max = 2.5;
    double  c2Min = 1.5;
    double  weightMax = 0.1;
    double  weightMin = 0.1;
    double  changeVelocity1 = -1;
    double changeVelocity2 = -1;

    // STEP 2.2. Create the quality indicator
    InvertedGenerationalDistance<PointSolution> igd = new InvertedGenerationalDistance<>();

    // STEP 2.3. Create the threshold for showing a changing in the Pareto front during the optimzation process
    UpdateThreshold<PointSolution> updateThreshold = new UpdateThreshold<>(0.005, igd);

   // STEP 2.4. Indicate whether the problem is updated automatically or not
    boolean updateProblemByIterations = false;
   // STEP 2.5. Create the evaluator
    SolutionListEvaluator<DoubleSolution> evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;

    algorithm = new DynamicSMPSO(problem,swarmSize,archive,mutation,maxIterations,r1Min,r1Max,r2Min,r2Max,c1Min,
            c1Max,c2Min,c2Max,weightMin,weightMax,changeVelocity1,changeVelocity2,evaluator,
            new DefaultObservable<>("Dynamic SMSPO"),
            updateThreshold,updateProblemByIterations);


   // STEP 3. Chart visualizator 
    RunTimeForDynamicProblemsChartObserver<DoubleSolution> runTimeChartObserver =
            new RunTimeForDynamicProblemsChartObserver<>("DynamicSMSPO", 80);

    // STEP 4. Streaming counter
    StreamingDataSource<Integer> streamingDataSource = new SimpleStreamingCounterDataSource(2000,problem);
    Thread thread =new Thread(streamingDataSource);
    thread.start();

    Thread algorithmThread = new Thread(algorithm);
    algorithmThread.join();

    // STEP 5. Register the chart visulizator in the problem in order to send the Pareto front
    algorithm.getObservable().register(runTimeChartObserver);

    // STEP 6. Execute the algorithm
    algorithmThread.start();
 }

Interactive Algorithm
---------------------

jMetal defines an interface for interactive algorithms, in this interface is described the method ``changeReferencePoints`` that is used for changing the reference points during the optimization process.
 
.. code-block:: java

 public interface InteractiveAlgorithm<S,R> extends Algorithm<R>{
    void  changeReferencePoints(List<List<Double>> newReferencePoints);
 }

Interactive Dynamic Multi-objective Runner
------------------------------------------

Like in dynamic multi-objective  when we execute an interactive dynamic multi-objective algorithm we need to configurate if the problem is updated by iterations or with a external counter and the chart visualizator. However, in this case we have to set how we modify the reference point during the optimization process. For that purpose, jMetal has the class ``KeyboardChangeReferencePoint`` which lets the user modifies the reference point by the keyboard.

.. code-block:: java

 public static void main(String[] args) throws Exception {

    // STEP 1. Create the problem
    DynamicProblem<DoubleSolution, Integer> problem = new FDA2();

    // STEP 2. Create the algorithm
    DynamicAlgorithm<List<DoubleSolution>>  algorithm;
    // STEP 2.1. Create the operators
    MutationOperator<DoubleSolution> mutation;

    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;
    List<List<Double>> referencePoints;
    referencePoints = new ArrayList<>();

    List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints = new ArrayList<>();

   
    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;


    int swarmSize = 100;
    int maxIterations = 250;

    double r1Max = 1.0;
    double r1Min = 0.0;
    double r2Max = 1.0;
    double r2Min = 0.0;
    double  c1Max = 2.5;
    double  c1Min = 1.5;
    double  c2Max = 2.5;
    double  c2Min = 1.5;
    double  weightMax = 0.1;
    double  weightMin = 0.1;
    double  changeVelocity1 = -1;
    double changeVelocity2 = -1;

    // STEP 2.3. Create the reference point
    referencePoints.add(Arrays.asList(0.0, 0.0));
    for (int i = 0; i < referencePoints.size(); i++) {
      archivesWithReferencePoints.add(
              new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(
                      swarmSize/referencePoints.size(), referencePoints.get(i))) ;
    }

   // STEP 2.4. Create the threshold for showing a changing in the Pareto front during the optimzation process
    UpdateThreshold<PointSolution> updateThreshold = new UpdateThreshold<>(0.005, igd);
   // STEP 2.5. Indicate whether the problem is updated automatically or not
    boolean updateProblemByIterations = false;
   // STEP 2.6. Create the evaluator
    SolutionListEvaluator<DoubleSolution> evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;

    algorithm = new DynamicSMPSORP(problem,swarmSize,archivesWithReferencePoints,
            referencePoints,mutation,maxIterations,r1Min,r1Max,r2Min,r2Max,c1Min,
            c1Max,c2Min,c2Max,weightMin,weightMax,changeVelocity1,changeVelocity2,evaluator,
            new DefaultObservable<>("Dynamic SMSPO"),
            updateThreshold,updateProblemByIterations);

    // STEP 3. Chart visualizator 
    RunTimeForDynamicProblemsChartObserver<DoubleSolution> runTimeChartObserver =
            new RunTimeForDynamicProblemsChartObserver<>("DynamicSMSPO", 80);
    runTimeChartObserver.setReferencePointList(referencePoints);

    // STEP 4. Streaming counter
    StreamingDataSource<Integer> streamingDataSource = new SimpleStreamingCounterDataSource(2000,problem);
    Thread thread =new Thread(streamingDataSource);
    thread.start();

    // STEP 5. Streaming keyboard
    StreamingDataSource<List<Integer>> keyboard =
            new KeyboardChangeReferencePoint((InteractiveAlgorithm) algorithm,referencePoints,runTimeChartObserver.getChart());
    Thread keyboardThread = new Thread(keyboard);
    keyboardThread.start();

    Thread algorithmThread = new Thread(algorithm);
    algorithmThread.join();

    // STEP 5. Register the chart visulizator in the problem in order to send the Pareto front
    algorithm.getObservable().register(runTimeChartObserver);

    // STEP 6. Execute the algorithm
    algorithmThread.start();
  }




