package evolutionary.algorithm;

import java.lang.reflect.Array;
import java.util.function.IntConsumer;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;

import evolutionary.generationCrator.INewGenerationCreator;
import optimization.algorithm.IPopulationOptimizationAlgorithm;
import optimization.utility.OptimizationAlgorithmsUtility;
import utilities.streamAndParallelization.PStreams;
import optimization.decoder.IDecoder;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.stopper.IOptimisationStopper;

public class GeneralGenerationAlgoritam<I,T> implements IPopulationOptimizationAlgorithm<T>{

	private final @Nonnull ToDoubleFunction<I> function;
	private final @Nonnull IDecoder<T,I> decoder;
	private final @Nonnull IStartPopulationGenerator<T> startPopulationGenerator;
	private final @Nonnull IFitnessEvaluator<T> evaluator;
	private final @Nonnull INewGenerationCreator<T> newGenerationCreator;
	private final @Nonnull IOptimisationStopper<T> evolutionStopper;
	private T[] population;

	public GeneralGenerationAlgoritam(@Nonnull ToDoubleFunction<I> function, @Nonnull IDecoder<T,I> decoder,
			@Nonnull IStartPopulationGenerator<T> startPopulationGenerator, @Nonnull IFitnessEvaluator<T> evaluator,
			@Nonnull INewGenerationCreator<T> newGenerationCreator, @Nonnull IOptimisationStopper<T> evolutionStopper) {
				this.function = function;
				this.decoder = decoder;
				this.startPopulationGenerator = startPopulationGenerator;
				this.evaluator = evaluator;
				this.newGenerationCreator = newGenerationCreator;
				this.evolutionStopper = evolutionStopper;
	}
	
	
	@Override
	public T run() {
		population = startPopulationGenerator.generate();
		double[] populationFunctionValues = new double[population.length];
		IntConsumer calculateFunctionValue = i -> {
			populationFunctionValues[i] = function.applyAsDouble(decoder.decode(population[i]));
		};
		PStreams.forEachIndexIn(population.length, false, calculateFunctionValue);
		double[] populationFitnesses = evaluator.evaluate(population, populationFunctionValues);
		
		@SuppressWarnings("unchecked")
		T[] newGenerationHolder = (T[]) Array.newInstance(population[0].getClass(), population.length);

		while(!evolutionStopper.shouldStop(population, populationFitnesses, populationFunctionValues)){

			newGenerationCreator.createNewGenerationFrom(population, newGenerationHolder, populationFunctionValues, populationFitnesses);
			
			System.arraycopy(newGenerationHolder, 0, population, 0, population.length);		
			PStreams.forEachIndexIn(population.length, false, calculateFunctionValue);
			populationFitnesses = evaluator.evaluate(population, populationFunctionValues);
		}
		
		return OptimizationAlgorithmsUtility.getBestSolution(population, populationFitnesses);
	}

	@Override
	public T[] getPopulation() {
		if(population!=null)
			return population;
		
		throw new RuntimeException("Population not initialized");
	}

}
