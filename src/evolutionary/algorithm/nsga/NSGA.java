package evolutionary.algorithm.nsga;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.Nonnull;

import evolutionary.algorithm.nsga.evaluator.NSGAFitnessEvaluator;
import evolutionary.crossing.ICrossing;
import evolutionary.mutation.IMutation;
import evolutionary.selection.ISelection;
import function.MOOPProblem;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.stopper.IOptimisationStopper;
import util.Relations;
import utilities.streamAndParallelization.PStreams;


public class NSGA<I,T> implements IOptimizationAlgorithm<T[]>{

	private final @Nonnull IStartPopulationGenerator<T> startPopulationGenerator;
	private final @Nonnull ISelection<T> selection;
	private final @Nonnull ICrossing<T> crossing;
	private final @Nonnull IMutation<T> mutation;
	private final @Nonnull NSGAFitnessEvaluator evaluator;
	private final @Nonnull IOptimisationStopper<T> stopper;
	private final @Nonnull IDecoder<T,I> decoder;
	private final @Nonnull MOOPProblem moop;

	public NSGA(IStartPopulationGenerator<T> startPopulationGenerator,
			NSGAFitnessEvaluator evaluator,
			ISelection<T> selection,
			ICrossing<T> crossing,
			IMutation<T> mutation,
			IDecoder<T,I> decoder,MOOPProblem moop,
			IOptimisationStopper<T> stopper) {
		this.startPopulationGenerator = startPopulationGenerator;
		this.evaluator = evaluator;
		this.selection = selection;
		this.crossing = crossing;
		this.mutation = mutation;
		this.decoder = decoder;
		this.moop = moop;
		this.stopper = stopper;
	}
	
	@Override
	public T[] run() {
		
		T[] population = startPopulationGenerator.generate();
		double[][] populationAsArray = asArrays(population);
		double[][] populationObjectiveValues = new double[population.length][moop.getNumberOfObjectives()];
		double[] populationFitnesses = new double[population.length];
		
		@SuppressWarnings("unchecked")
		T[] nextPopulation = (T[]) Array.newInstance(population[0].getClass(), population.length);
		
		selection.setNumberOfChromosomesToSelect(population.length*2);
		calculateObjectiveValues(populationAsArray,populationObjectiveValues);
		do{
			
			List<LinkedHashSet<Integer>> fronts = sort(populationObjectiveValues);
			evaluator.evaluate(populationAsArray, populationObjectiveValues, populationFitnesses,fronts);
						
			int nextPopulationSize = 0;
			do{
				T[] parents = selection.selectFrom(population, populationFitnesses);
				T[] children = crossing.cross(parents);
				mutation.mutate(children);
				for(int i=0;i<children.length && i+nextPopulationSize<nextPopulation.length;i++){
					nextPopulation[nextPopulationSize] =  children[i];
					nextPopulationSize++;
				}
				
			}while(nextPopulationSize<population.length);
			
			T[] t = nextPopulation;
			nextPopulation = population;
			population = t;
			populationAsArray = asArrays(population);
			calculateObjectiveValues(populationAsArray,populationObjectiveValues);
			
			
		}while(!stopper.shouldStop(population, populationFitnesses, populationFitnesses));
		
		return population;
	}

	public List<LinkedHashSet<Integer>> sort(double[][] populationObjectiveValues) {
		return Relations.undominatedSortByIndex(populationObjectiveValues);
	}

	public double[][] asArrays(T[] population) {
		return Arrays.stream(population).map(unit->decoder.decode(unit)).toArray(double[][]::new);
	}

	public void calculateObjectiveValues(double[][] population, double[][] populationObjectiveValues) {
		PStreams.forEachIndexIn(population.length, true,i->{
			moop.evaluate(population[i], populationObjectiveValues[i]);
		});
	}

}
