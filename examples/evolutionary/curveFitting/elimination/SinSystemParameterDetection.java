package evolutionary.curveFitting.elimination;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


import dataset.handeling.DataSetLoader;
import evolutionary.algorithm.gaElimination.GeneralEliminationAlgorithm;
import evolutionary.crossing.BLXAlphaCrossing;
import evolutionary.crossing.ICrossing;
import evolutionary.mutation.CompositeMutation;
import evolutionary.mutation.ComponentChangeMutation;
import evolutionary.mutation.IMutation;
import evolutionary.mutation.NormalShiftMutation;
import function.decorators.DimensionFocusWrapper;
import function.decorators.FunctionCallCounterWrapper;
import function.error.PrototypeBasedSystemLossFunction;
import function.error.SquareErrorFunction;
import function.prototype.SinFunction;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.algorithm.decorator.TimedOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.decoder.PassThroughDoubleDecoder;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.fittnesEvaluator.observable.BestObserver;
import optimization.fittnesEvaluator.observable.PerChromosomeObservableFitnessEvaluator;
import optimization.fittnesEvaluator.observable.PrintBestObserver;
import optimization.fittnesEvaluator.observable.PrototypeGraphngBestObserver;
import optimization.fittnesEvaluator.observable.SleepOnBestObserver;
import optimization.solution.DoubleArraySolution;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.startPopulationGenerator.RandomStartPopulationGenerator;
import optimization.stopper.CompositeOptimisationStopper;
import optimization.stopper.FunctionValueStopper;
import optimization.stopper.GenerationNumberEvolutionStopper;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.AlgorithmsPresentationUtility;
import ui.graph.SimpleGraph;

public class SinSystemParameterDetection {

	private SinSystemParameterDetection() {}
	
	public static void main(String[] args) throws IOException {
		
		int variableMinValue = 0;
		int variableMaxValue = 2;
		int populationSize = 100;
		double alhpa = 1.1;
		double mutationChance = 0.02;
		double sigm = 0.1;
		double acceptableErrorRate = 10e-6;
		int maximumNumberOfGenerations = 500_000_000;
		int tournamentSize = 3;
		
		//Function to optimize
		SinFunction sinFunction = new SinFunction();
		int variableNumber = sinFunction.getCoefficientCount();
		double[][] systemMatrix = DataSetLoader.loadMatrix(new File(System.getProperty("user.dir"),"data/algorithm-examples/sin-data.txt"));
		FunctionCallCounterWrapper<double[]> function =  new FunctionCallCounterWrapper<>(
				new PrototypeBasedSystemLossFunction(systemMatrix,sinFunction,new SquareErrorFunction())
				);
		
		
		//Start UI
		SimpleGraph graph = new SimpleGraph(10,10,1,1);
		graph.addFunction(
				new DimensionFocusWrapper(sinFunction, 0, new double[1]),
			Color.BLUE);
		for(double[] row:systemMatrix){
			graph.addPoint(row[0], row[1]);
		}
		graph.display();

		//Decoder
		IDecoder<DoubleArraySolution,double[]> decoder = new PassThroughDoubleDecoder();				

		//Population generator
		IStartPopulationGenerator<DoubleArraySolution> startPopulationGenerator = new RandomStartPopulationGenerator(populationSize, 
				variableNumber, variableMinValue, variableMaxValue);
				
		//Fitness evaluator
		PerChromosomeObservableFitnessEvaluator<DoubleArraySolution> evaluator = new PerChromosomeObservableFitnessEvaluator<>(v->
			 ThroughOneFitnessEvaluator.evaluationMethod.applyAsDouble(
						FunctionValueFitnessEvaluator.evaluationMethod.applyAsDouble(v)
		));
		int sleepTimeInMs = 500;
		evaluator.addObserver(new BestObserver<>(decoder, Arrays.asList(
				new PrintBestObserver<DoubleArraySolution,double[]>(System.out),
				new PrototypeGraphngBestObserver<DoubleArraySolution>(sinFunction, graph),
				//TODO: include to watch step by step progress
				new SleepOnBestObserver<DoubleArraySolution,double[]>(sleepTimeInMs)
		)));
		
		//Crossing
		ICrossing<DoubleArraySolution> crossing = new BLXAlphaCrossing(alhpa, variableMinValue, variableMaxValue);	
		
		//Mutation
		IMutation<DoubleArraySolution> mutation = new CompositeMutation<>(Arrays.asList(
				new NormalShiftMutation(mutationChance, sigm,variableMinValue,variableMaxValue),
				new ComponentChangeMutation(mutationChance, variableMinValue, variableMaxValue)
				));
		//Optimization stopper
		IOptimisationStopper<DoubleArraySolution> evolutionStopper = new CompositeOptimisationStopper<>(Arrays.asList(
			new FunctionValueStopper<>(acceptableErrorRate),
			new GenerationNumberEvolutionStopper<>(maximumNumberOfGenerations)
		));
				
		//Optimization
		IOptimizationAlgorithm<DoubleArraySolution> optimizationAlgorithm =
				new GeneralEliminationAlgorithm<>(startPopulationGenerator, tournamentSize, crossing, mutation, decoder, function, evaluator, evolutionStopper);
		TimedOptimizationAlgorithm<DoubleArraySolution> timedOptAlgorithm = new TimedOptimizationAlgorithm<>(optimizationAlgorithm);
		
		//Solution presentation
		DoubleArraySolution solution = timedOptAlgorithm.run();
		System.out.println();
		AlgorithmsPresentationUtility.printExecutionTime(timedOptAlgorithm.getExecutionTime());
		System.out.println("Solution: "  + solution);
		System.out.println("Error: " + function.applyAsDouble(solution.getValues()));
		AlgorithmsPresentationUtility.printEvaluationCount(function.getEvaluationCount());
	}

}
