package evolutionary.algorithm.de;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.ToDoubleFunction;

import evolutionary.algorithm.de.baseVectorSelection.IBaseVectorSelection;
import evolutionary.algorithm.de.crossing.IDeCrossing;
import evolutionary.algorithm.de.linearCombinationProvider.IDeLinearCombinationProvider;
import evolutionary.algorithm.de.mutantCreator.IDeMutantCreator;
import evolutionary.util.EvolutionUtil;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.OptimizationAlgorithmsUtility;
import utilities.streamAndParallelization.AbstractParallelizable;
import utilities.streamAndParallelization.PStreams;

/**
 * {@link DE} evolutionary algorithm which can be used for optimization. <br>
 * Basic logic: <br>
 * <ol>
 * <li>Create start population</li>
 * <li>
 * Do until not satisfied: <br>
 * <ol>
 * <li>
 * For each vector:
 * <ol>
 * <li>Say the current vector is the target</li>
 * <li>Generate the base and mutate it. The result is the mutant vector</li>
 * <li>Cross the target vector and the mutant vector. The result is the trial vector</li>
 * <li>Select between the trial vector and the target vector for the next generation</li>
 * </ol>
 * </li>
 * <li>Make the new population the current population</li>
 * </ol>
 * </li>
 * </ol>
 *
 * @param <T>
 */
public class DE<I,T> extends AbstractParallelizable implements IOptimizationAlgorithm<T> {

	private final IStartPopulationGenerator<T> startPopulationGenerator;
	private final IDecoder<T,I> decoder;
	private IOptimisationStopper<T> stopper;
	private final ToDoubleFunction<I> function;
	private final IFitnessEvaluator<T> evaluator;
	private final IBaseVectorSelection<T> baseVectorSelection;
	private final IDeLinearCombinationProvider<T> linearCombinationProvider;
	private final IDeMutantCreator<T> mutantCreator;
	private final IDeCrossing<T> crossing;

	public DE(
			IStartPopulationGenerator<T> startPopulationGenerator, 
			IBaseVectorSelection<T> baseVectorSelection,
			IDeLinearCombinationProvider<T> linearCombinationProvider,
			IDeMutantCreator<T> mutantCreator,
			IDeCrossing<T> crossing,
			IDecoder<T,I> decoder, IOptimisationStopper<T> stopper, 
			ToDoubleFunction<I> function, 
			IFitnessEvaluator<T> evaluator) {
		this.startPopulationGenerator = startPopulationGenerator;
		this.baseVectorSelection = baseVectorSelection;
		this.linearCombinationProvider = linearCombinationProvider;
		this.mutantCreator = mutantCreator;
		this.crossing = crossing;
		this.decoder = decoder;
		this.stopper = stopper;
		this.function = function;
		this.evaluator = evaluator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T run() {

		T[] population = startPopulationGenerator.generate();
		double[] populationFunctionValues = new double[population.length];
		double[] populationFitnesses = new double[population.length];
		
		EvolutionUtil.evaluatePerChromosome(population, populationFunctionValues, populationFitnesses, function,
				decoder, evaluator, parallel);

		T[] nextPopulation = (T[]) Array.newInstance(population[0].getClass(), population.length);
		double[] nextPopulationFunctionValues = new double[population.length];
		double[] nextPopulationFitnesses = new double[population.length];

		Function<T[], Function<double[],Function<double[],Function<T[],Function<double[],Function<double[],IntConsumer>>>>>> nextGenerationCreator =
				finalPopulation -> finalPopulationFunctionValues -> finalPopulationFitnesses -> finalNextPopulation -> finalNextPopulationFunctionValues -> finalNextPopulationFitnesses -> i -> {

					T target = finalPopulation[i];
					
					int baseIndex = baseVectorSelection.select(i,finalPopulation, finalPopulationFitnesses, finalPopulationFunctionValues);
					T[] linearCombinations = linearCombinationProvider.provideFrom(finalPopulation, finalPopulationFitnesses, finalPopulationFunctionValues,baseIndex);
					
					T mutant = mutantCreator.createFrom(finalPopulation[baseIndex], linearCombinations);
					
					T trial = crossing.cross(target, mutant);
					double trialFunctionValue = function.applyAsDouble(decoder.decode(trial));
					double trialFitness = evaluator.evaluate(trial, trialFunctionValue);
					
					if(trialFitness>=finalPopulationFitnesses[i]){
						finalNextPopulation[i] = trial;
						finalNextPopulationFunctionValues[i] = trialFunctionValue;
						finalNextPopulationFitnesses[i] = trialFitness;
					}
					else{
						finalNextPopulation[i] = finalPopulation[i];
						finalNextPopulationFunctionValues[i] = finalPopulationFunctionValues[i];
						finalNextPopulationFitnesses[i] = finalPopulationFitnesses[i];
					}

				};

		
		do {
						
			PStreams.forEachIndexIn(population.length, parallel,
					nextGenerationCreator
					.apply(population)
					.apply(populationFunctionValues)
					.apply(populationFitnesses)
					.apply(nextPopulation)
					.apply(nextPopulationFunctionValues)
					.apply(nextPopulationFitnesses));			
			
			T[] pSwithcHelp = population;
			double[] fSwitchHelp = populationFitnesses;
			double[] fVSwitchHelp = populationFunctionValues;
			
			population = nextPopulation;
			populationFunctionValues = nextPopulationFunctionValues;
			populationFitnesses = nextPopulationFitnesses;

			nextPopulation = pSwithcHelp;
			nextPopulationFunctionValues = fVSwitchHelp;
			nextPopulationFitnesses = fSwitchHelp;

		} while (!stopper.shouldStop(population, populationFitnesses, populationFunctionValues));

		return OptimizationAlgorithmsUtility.getBestSolution(population, populationFitnesses);
	}

}
