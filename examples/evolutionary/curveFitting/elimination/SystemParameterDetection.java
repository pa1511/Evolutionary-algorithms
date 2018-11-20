package evolutionary.curveFitting.elimination;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import evolutionary.algorithm.GeneralGenerationAlgoritam;
import evolutionary.crossing.BLXAlphaCrossing;
import evolutionary.crossing.ICrossing;
import evolutionary.generationCrator.INewGenerationCreator;
import evolutionary.generationCrator.implementations.CanonicalGenerationCreator;
import evolutionary.mutation.IMutation;
import evolutionary.mutation.NormalShiftMutation;
import evolutionary.selection.ISelection;
import evolutionary.selection.TournamentSelection;
import function.decorators.FunctionCallCounterWrapper;
import function.error.PrototypeBasedSystemLossFunction;
import function.error.SquareErrorFunction;
import function.prototype.APrototypeFunction;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.algorithm.decorator.TimedOptimizationAlgorithm;
import optimization.decoder.PassThroughDoubleDecoder;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.fittnesEvaluator.observable.BestObserver;
import optimization.fittnesEvaluator.observable.PerChromosomeObservableFitnessEvaluator;
import optimization.fittnesEvaluator.observable.PrintBestObserver;
import optimization.solution.DoubleArraySolution;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.startPopulationGenerator.RandomStartPopulationGenerator;
import optimization.stopper.CompositeOptimisationStopper;
import optimization.stopper.FunctionValueStopper;
import optimization.stopper.GenerationNumberEvolutionStopper;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.AlgorithmsPresentationUtility;

public class SystemParameterDetection {

	private SystemParameterDetection() {}
	
	public static void main(String[] args) throws IOException {
		
		double mutationChance = 0.05;
		double sigm = 0.2;
		double acceptableErrorRate = 10e-6;
		int maximumNumberOfGenerations = 1000000;
		int tournamentSize = 3;
		double alhpa = 1.2;
		int populationSize = 50;
		int variableMinValue = -4;
		int variableMaxValue = 4;
		int variableNumber = 5;
		
		//Population generator
		IStartPopulationGenerator<DoubleArraySolution> startPopulationGenerator = new RandomStartPopulationGenerator(populationSize, 
				variableNumber, variableMinValue, variableMaxValue);

		//Decoder
		PassThroughDoubleDecoder decoder = new PassThroughDoubleDecoder();

		//Fitness evaluator
		PerChromosomeObservableFitnessEvaluator<DoubleArraySolution> evaluator = new PerChromosomeObservableFitnessEvaluator<>(v->
			 ThroughOneFitnessEvaluator.evaluationMethod.applyAsDouble(
						FunctionValueFitnessEvaluator.evaluationMethod.applyAsDouble(v)
		));
		evaluator.addObserver(new BestObserver<>(decoder, Arrays.asList(
				new PrintBestObserver<DoubleArraySolution,double[]>(System.out)
		)));
		
		//Crossing
		ICrossing<DoubleArraySolution> crossing = new BLXAlphaCrossing(alhpa, variableMinValue, variableMaxValue);	
		
		//Mutation
		IMutation<DoubleArraySolution> mutation = new NormalShiftMutation(mutationChance, sigm,variableMinValue,variableMaxValue);
				
		//Optimization stopper
		IOptimisationStopper<DoubleArraySolution> evolutionStopper = new CompositeOptimisationStopper<>(Arrays.asList(
			new FunctionValueStopper<>(acceptableErrorRate),
			new GenerationNumberEvolutionStopper<>(maximumNumberOfGenerations)
		));

		//Function to optimize
		List<String> lines = Files.readAllLines(new File(System.getProperty("user.dir"),"data/algorithm-examples/system-dataset.txt").toPath());
		double[][] systemMatrix = parse(lines);
		FunctionCallCounterWrapper<double[]> function =  new FunctionCallCounterWrapper<>(new PrototypeBasedSystemLossFunction(systemMatrix,new APrototypeFunction(5) {
			
			@Override
			public double applyAsDouble(double[] value) {
				double b0 = coef[0];
				double b1 = coef[1];
				double b2 = coef[2];
				double b3 = coef[3];
				double b4 = coef[4];
				
				double x = value[0];
				double y = value[1];
				
						
				return Math.sin(b0+b1*x)+b2*Math.cos(x*(b3+y))*(1/(1+Math.pow(Math.E, Math.pow(x-b4,2))));
			}
			
			@Override
			public int getVariableCount() {
				return 2;
			}
			
		},new SquareErrorFunction()));

		
		//Optimization
		IOptimizationAlgorithm<DoubleArraySolution> optimizationAlgorithm;
		
		//Selection
		ISelection<DoubleArraySolution> selection = new TournamentSelection<>(tournamentSize, populationSize*2);

		//New generation creator
		INewGenerationCreator<DoubleArraySolution> newGenerationCreator = new CanonicalGenerationCreator<>(selection, crossing, mutation);
		newGenerationCreator.setElitists(true);
		newGenerationCreator.setAllowDuplicates(false);

		//Optimization
		optimizationAlgorithm = new GeneralGenerationAlgoritam<>(function, 
		decoder, startPopulationGenerator, evaluator, 
		newGenerationCreator, 
		evolutionStopper);
		
		TimedOptimizationAlgorithm<DoubleArraySolution> timedOptAlgorithm = new TimedOptimizationAlgorithm<>(optimizationAlgorithm);
		
		//Solution presentation
		DoubleArraySolution solution = timedOptAlgorithm.run();
		System.out.println();
		AlgorithmsPresentationUtility.printExecutionTime(timedOptAlgorithm.getExecutionTime());
		System.out.println("Solution: "  + solution);
		System.out.println("Error: " + function.applyAsDouble(solution.getValues()));
		AlgorithmsPresentationUtility.printEvaluationCount(function.getEvaluationCount());
	}

	private static @Nonnull double[][] parse(@Nonnull List<String> lines){
		
		List<String[]> equatioList = lines.stream().map(line->line.replaceAll("\\t", " ").split(" ")).collect(Collectors.toList());
		
		double[][] result = new double[equatioList.size()][equatioList.get(0).length];
		
		for(int i=0,size=equatioList.size();i<size;i++){
			String[] equation = equatioList.get(i);
			for(int j=0; j<equation.length; j++){
				result[i][j] = Double.parseDouble(equation[j].trim());
			}
		}
		
		return result;
	
	}	
}
