package evolutionary.optimumDetection.elimination;

import java.util.Arrays;

import evolutionary.algorithm.gaElimination.GeneralEliminationAlgorithm;
import evolutionary.crossing.BLXAlphaCrossing;
import evolutionary.crossing.CompositeCrossing;
import evolutionary.crossing.DiscreteCrossing;
import evolutionary.crossing.ICrossing;
import evolutionary.crossing.WholeArithmeticRecombination;
import evolutionary.mutation.CompositeMutation;
import evolutionary.mutation.ComponentChangeMutation;
import evolutionary.mutation.IMutation;
import evolutionary.mutation.NormalShiftMutation;
import function.common.benchmark.BealesFunction;
import function.decorators.FunctionCallCounterWrapper;
import optimization.algorithm.decorator.TimedOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.decoder.PassThroughDoubleDecoder;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.fittnesEvaluator.observable.BestObserver;
import optimization.fittnesEvaluator.observable.ObservableFitnessEvaluator;
import optimization.fittnesEvaluator.observable.PerChromosomeObservableFitnessEvaluator;
import optimization.fittnesEvaluator.observable.PrintBestObserver;
import optimization.solution.DoubleArraySolution;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.startPopulationGenerator.RandomStartPopulationGenerator;
import optimization.stopper.CompositeOptimisationStopper;
import optimization.stopper.FunctionEvaluationCountStopper;
import optimization.stopper.FunctionValueStopper;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.AlgorithmsPresentationUtility;

public class BealesFunctionOptimumDetection {

	private BealesFunctionOptimumDetection() {}
	
	public static void main(String[] args) {
		
		boolean shouldPrintSteps = true;

		int maxFunctionEvaluations = 500_000_000;
		double mutationChance = 0.05;
		int populationSize = 500;

		//Parameters
		double precision = 1e-7;
		int tournamentSize = 4;
		double alpha = 1.2;
		double sigm = 0.5;
								
		//Function
		BealesFunction function= new BealesFunction();
		int numberOfVariables = function.getVariableCount();
		double[] standardSearchMin = function.getStandardSearchMin();
		double[] standardSearchMax = function.getStandardSearchMax();
		
		FunctionCallCounterWrapper<double[]> wrappedFunction = new FunctionCallCounterWrapper<>(function);

		//Decoder
		IDecoder<DoubleArraySolution,double[]> decoder = new PassThroughDoubleDecoder(); 
				
		//Start population generator
		IStartPopulationGenerator<DoubleArraySolution> startPopulationGenerator = 
				new RandomStartPopulationGenerator(populationSize, numberOfVariables,standardSearchMin, standardSearchMax);

		//Evaluator
		IFitnessEvaluator<DoubleArraySolution> evaluator;
		if(shouldPrintSteps){
			evaluator = new PerChromosomeObservableFitnessEvaluator<>(v->
				ThroughOneFitnessEvaluator.evaluationMethod.applyAsDouble(
							FunctionValueFitnessEvaluator.evaluationMethod.applyAsDouble(v)
						)
			);
			((ObservableFitnessEvaluator<DoubleArraySolution>) evaluator).addObserver(new BestObserver<>(decoder, Arrays.asList(
					new PrintBestObserver<DoubleArraySolution,double[]>(System.out)
			)));
		}
		else{
			evaluator = new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>());
		}
		
		//Crossing
		ICrossing<DoubleArraySolution> crossing = new CompositeCrossing<DoubleArraySolution>(Arrays.asList(
				new BLXAlphaCrossing(alpha, standardSearchMin, standardSearchMax),
				new DiscreteCrossing(0.5),
				new WholeArithmeticRecombination()));
		
		//Mutation
		IMutation<DoubleArraySolution> mutation = new CompositeMutation<>(Arrays.asList(
				new NormalShiftMutation(mutationChance, sigm, standardSearchMin, standardSearchMax),
				new ComponentChangeMutation(mutationChance, standardSearchMin[0], standardSearchMax[0])
				));

		//Optimization stopper
		IOptimisationStopper<DoubleArraySolution> evolutionStopper = new CompositeOptimisationStopper<>(Arrays.asList(
				new FunctionValueStopper<>(precision),
				new FunctionEvaluationCountStopper<>(wrappedFunction, maxFunctionEvaluations)
		));
		
		//Optimization algorithm
		GeneralEliminationAlgorithm<double[],DoubleArraySolution> eliminationAlgorithm = 
				new GeneralEliminationAlgorithm<>(startPopulationGenerator, tournamentSize, crossing, mutation, decoder, wrappedFunction, evaluator, evolutionStopper);
		eliminationAlgorithm.setParallel(true);		
		TimedOptimizationAlgorithm<DoubleArraySolution> timed = new TimedOptimizationAlgorithm<>(eliminationAlgorithm);
		
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
