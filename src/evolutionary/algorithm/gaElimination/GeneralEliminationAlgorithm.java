package evolutionary.algorithm.gaElimination;

import java.lang.reflect.Array;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import evolutionary.crossing.ICrossing;
import evolutionary.mutation.IMutation;
import evolutionary.selection.TournamentSelection;
import evolutionary.util.EvolutionUtil;
import optimization.algorithm.IPopulationOptimizationAlgorithm;
import optimization.utility.OptimizationAlgorithmsUtility;
import utilities.streamAndParallelization.AbstractParallelizable;
import utilities.streamAndParallelization.PStreams;
import optimization.decoder.IDecoder;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.stopper.IOptimisationStopper;

/**
 * Be careful when using the parallel version because there is to synchronization
 */
public class GeneralEliminationAlgorithm<I,T> extends AbstractParallelizable implements IPopulationOptimizationAlgorithm<T> {

	private final @Nonnull ToDoubleFunction<I> function;
	private final @Nonnull IDecoder<T,I> decoder;
	private final @Nonnull IStartPopulationGenerator<T> startPopulationGenerator;
	private final @Nonnull IFitnessEvaluator<T> evaluator;
	private final @Nonnull IOptimisationStopper<T> evolutionStopper;
	private final @Nonnull ICrossing<T> crossing;
	private final @Nonnull IMutation<T> mutation;
	private final @Nonnegative int tournamentSize;
	private final @Nonnegative int repeatLimit = 250;
	private T[] population;

	public GeneralEliminationAlgorithm(@Nonnull IStartPopulationGenerator<T> startPopulationGenerator,int tournamentSize,
			@Nonnull ICrossing<T> crossing,@Nonnull IMutation<T> mutation,
			@Nonnull IDecoder<T,I> decoder, @Nonnull ToDoubleFunction<I> function,  @Nonnull IFitnessEvaluator<T> evaluator,
			@Nonnull IOptimisationStopper<T> evolutionStopper) {
		this.tournamentSize = tournamentSize;
		this.function = function;
		this.decoder = decoder;
		this.startPopulationGenerator = startPopulationGenerator;
		this.evaluator = evaluator;
		this.crossing = crossing;
		this.mutation = mutation;
		this.evolutionStopper = evolutionStopper;
	}
			
	@Override
	@SuppressWarnings("unchecked")
	public T run() {
				
		population = startPopulationGenerator.generate();
		double[] populationFunctionValues = new double[population.length];
		double[] populationFitnesses = new double[population.length];
		EvolutionUtil.evaluatePerChromosome(population, populationFunctionValues, populationFitnesses, function, decoder, evaluator, true);
		
		T[] parents = (T[]) Array.newInstance(population[0].getClass(), tournamentSize-1);
		
		Runnable evolutionTask = ()->{
			T[] children = (T[]) Array.newInstance(population[0].getClass(), tournamentSize/2);
			int repeat = 0;
			
			while (!evolutionStopper.shouldStop(population, populationFitnesses, populationFunctionValues)) {
				int[] selectedUnitIndexes = TournamentSelection.selectTournamentParticipants(population,tournamentSize);
				int worstParentIndex = TournamentSelection.runTournament(populationFitnesses, selectedUnitIndexes,true);
		
				for(int i=0,j=0; i<parents.length;i++,j++){
					if(selectedUnitIndexes[j]==worstParentIndex){
						i--;
					}
					else{
						parents[i] = population[selectedUnitIndexes[j]];
					}
				}
				
				
				crossing.cross(parents, children);
				
				for(int i=0; i<children.length; i++){
					T child = children[i];
					mutation.mutate(child);
					double childValue = function.applyAsDouble(decoder.decode(child));
					double childFitness = evaluator.evaluate(child, childValue);
					
					repeat++;
					
					if(childFitness>=populationFitnesses[worstParentIndex] || repeat>repeatLimit){
						children[i] = population[worstParentIndex];
						
						population[worstParentIndex] = child;
						populationFunctionValues[worstParentIndex] = childValue;
						populationFitnesses[worstParentIndex] = childFitness;
						repeat = 0;
						break;
					}
				}
			}
		};
		
		if(parallel){
			PStreams.onEachSystemCore(evolutionTask);
		}
		else{
			evolutionTask.run();
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
