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
import function.common.benchmark.SchaffersFunction6;
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

public class Schaffers6FunctionOptimumDetection {

	private Schaffers6FunctionOptimumDetection() {}
	
	public static void main(String[] args) {
		
		boolean shouldPrintSteps = true;
				
		//User provided data
		int maxFunctionEvaluations = 500_000_000;
		int numberOfVariables = 6;
		double mutationChance = 0.05;
		int populationSize = 500;
		double alpha = 1.25;
		double sigm = 1;

		//Parameters
		double precision = 5e-5;
		int tournamentSize = 20;
		
		//Variable limitations
		double min = -100;
		double max = -1*min;
						
		//Function
		SchaffersFunction6 function = new SchaffersFunction6(numberOfVariables);
		
		FunctionCallCounterWrapper<double[]> wrappedFunction = new FunctionCallCounterWrapper<>(function);

		//Decoder
		IDecoder<DoubleArraySolution,double[]> decoder = new PassThroughDoubleDecoder(); 
				
		//Start population generator
		IStartPopulationGenerator<DoubleArraySolution> startPopulationGenerator = 
				new RandomStartPopulationGenerator(populationSize, numberOfVariables, min, max);

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
				new BLXAlphaCrossing(alpha, min, max),
				new DiscreteCrossing(0.5),
				new WholeArithmeticRecombination()));
		
		//Mutation
		IMutation<DoubleArraySolution> mutation = new CompositeMutation<>(Arrays.asList(
				new NormalShiftMutation(mutationChance, sigm, min, max),
				new ComponentChangeMutation(mutationChance, min, max)
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
