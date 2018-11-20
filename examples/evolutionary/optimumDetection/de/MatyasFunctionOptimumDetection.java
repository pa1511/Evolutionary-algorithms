package evolutionary.optimumDetection.de;

import java.util.Arrays;

import evolutionary.algorithm.de.DE;
import evolutionary.algorithm.de.baseVectorSelection.BestBaseVectorSelection;
import evolutionary.algorithm.de.baseVectorSelection.IBaseVectorSelection;
import evolutionary.algorithm.de.crossing.DeExponentialDoubleArrayCrossing;
import evolutionary.algorithm.de.crossing.IDeCrossing;
import evolutionary.algorithm.de.linearCombinationProvider.IDeLinearCombinationProvider;
import evolutionary.algorithm.de.linearCombinationProvider.TargetToBesDoubleArraytLinearCombinationProvider;
import evolutionary.algorithm.de.mutantCreator.DoubleArrayMutantCreator;
import evolutionary.algorithm.de.mutantCreator.IDeMutantCreator;
import function.common.benchmark.MatyasFunction;
import function.decorators.FunctionCallCounterWrapper;
import optimization.algorithm.decorator.TimedOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.decoder.PassThroughDoubleDecoder;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.fittnesEvaluator.observable.Best2DUnitGraphngObserver;
import optimization.fittnesEvaluator.observable.BestObserver;
import optimization.fittnesEvaluator.observable.PerChromosomeObservableFitnessEvaluator;
import optimization.fittnesEvaluator.observable.PrintBestObserver;
import optimization.fittnesEvaluator.observable.SleepOnBestObserver;
import optimization.solution.DoubleArraySolution;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.startPopulationGenerator.RandomStartPopulationGenerator;
import optimization.stopper.CompositeOptimisationStopper;
import optimization.stopper.FunctionEvaluationCountStopper;
import optimization.stopper.FunctionValueStopper;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.AlgorithmsPresentationUtility;
import ui.graph.SimpleGraph;

public class MatyasFunctionOptimumDetection {

	private MatyasFunctionOptimumDetection() {}
	
	public static void main(String[] args) {
		
		int maxFunctionEvaluations = 500_000_000;
		int populationSize = 500;

		//Parameters
		double precision = 1e-7;
		int linearCombinationCount = 4;
								
		//Function
		MatyasFunction function= new MatyasFunction();
		int numberOfVariables = function.getVariableCount();
		double[] standardSearchMin = function.getStandardSearchMin();
		double[] standardSearchMax = function.getStandardSearchMax();
		
		FunctionCallCounterWrapper<double[]> wrappedFunction = new FunctionCallCounterWrapper<>(function);

		//Start UI
		SimpleGraph graph = new SimpleGraph(8,8);
		graph.display();
		
		//Decoder
		IDecoder<DoubleArraySolution,double[]> decoder = new PassThroughDoubleDecoder(); 
				
		//Start population generator
		IStartPopulationGenerator<DoubleArraySolution> startPopulationGenerator = 
				new RandomStartPopulationGenerator(populationSize, numberOfVariables,standardSearchMin, standardSearchMax);
		
		//Crossing
		double crossingChance = 0.5;
		IDeCrossing<DoubleArraySolution> crossing = new DeExponentialDoubleArrayCrossing(crossingChance);
		
		//BaseVector selection
		IBaseVectorSelection<DoubleArraySolution> baseVectorSelection = new BestBaseVectorSelection<DoubleArraySolution>();
		
		//Linear combination provider
		IDeLinearCombinationProvider<DoubleArraySolution> linearCombinationProvider =  new TargetToBesDoubleArraytLinearCombinationProvider(linearCombinationCount);
		
		//Mutant creator
		double f = 1.2;
		IDeMutantCreator<DoubleArraySolution> mutantCreator = new DoubleArrayMutantCreator(f );

		//Evaluator
		PerChromosomeObservableFitnessEvaluator<DoubleArraySolution> evaluator = new PerChromosomeObservableFitnessEvaluator<>(v->
				ThroughOneFitnessEvaluator.evaluationMethod.applyAsDouble(
							FunctionValueFitnessEvaluator.evaluationMethod.applyAsDouble(v)
						)
			);
		int sleepTimeInMs = 50;
		evaluator.addObserver(new BestObserver<>(decoder, Arrays.asList(
				new PrintBestObserver<DoubleArraySolution,double[]>(System.out),
				new Best2DUnitGraphngObserver<>(graph),
				//TODO: include to watch step by step progress
				new SleepOnBestObserver<DoubleArraySolution,double[]>(sleepTimeInMs)
		)));
				
		//Optimization stopper
		IOptimisationStopper<DoubleArraySolution> stopper = new CompositeOptimisationStopper<>(Arrays.asList(
				new FunctionValueStopper<>(precision),
				new FunctionEvaluationCountStopper<>(wrappedFunction, maxFunctionEvaluations)
		));
				
		//Optimization algorithm
		DE<double[],DoubleArraySolution> deAlgorithm = 
		new DE<double[],DoubleArraySolution>(startPopulationGenerator, 
				baseVectorSelection, linearCombinationProvider, mutantCreator, 
				crossing, decoder, stopper, wrappedFunction, evaluator);
		deAlgorithm.setParallel(true);
		TimedOptimizationAlgorithm<DoubleArraySolution> timed = new TimedOptimizationAlgorithm<>(deAlgorithm);
		
		//Algorithm execution
		DoubleArraySolution solution = timed.run();
		
		//Print results
		System.out.println(function.toString());
		AlgorithmsPresentationUtility.printEvaluationCount(wrappedFunction.getEvaluationCount());
		AlgorithmsPresentationUtility.printExecutionTime(timed.getExecutionTime());
		double[] decodedSolution = solution.values;
		double value = wrappedFunction.applyAsDouble(decodedSolution);
		System.out.println("Solution: " + Arrays.toString(decodedSolution));
		System.out.println("Optimum solution: " + Arrays.toString(function.getMinValueCoordinates()));
		System.out.println("Value: " + value + "\n");
	}

}
