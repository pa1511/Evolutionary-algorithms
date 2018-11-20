package evolutionary.algorithm;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;

import evolutionary.mutation.ProportionalMutation;
import evolutionary.util.EvolutionUtil;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.limitter.OptimisationLimmiter;
import optimization.solution.IClonable;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import utilities.streamAndParallelization.AbstractParallelizable;

public class ClonAlg<I,T extends IClonable<T>> extends AbstractParallelizable implements IOptimizationAlgorithm<T>{
	
	private @Nonnull IStartPopulationGenerator<T> startPopulationGenerator;
	private @Nonnull IStartPopulationGenerator<T> dPopulationGenerator;
	private @Nonnull ProportionalMutation<T> mutation;
	private @Nonnull OptimisationLimmiter<T> limmiter;
	private @Nonnull final Function<T[], IntConsumer> evaluate;
	private double beta;
	
	public ClonAlg(ToDoubleFunction<I> function,	IDecoder<T,I> decoder,
			IStartPopulationGenerator<T> startPopulationGenerator, 
			ProportionalMutation<T> mutation, IFitnessEvaluator<T> fitnessEvaluator,
			OptimisationLimmiter<T> stopper,
			IStartPopulationGenerator<T> dPopulationGenerator, double beta) {
		
		this.startPopulationGenerator = startPopulationGenerator;
		this.mutation = mutation;
		this.limmiter = stopper;
		this.dPopulationGenerator = dPopulationGenerator;
		this.beta = beta;
		
		evaluate = population -> i -> {
			double value  = function.applyAsDouble(decoder.decode(population[i]));
			double qulity = fitnessEvaluator.evaluate(population[i], value);
			population[i].setValue(value);
			population[i].setQuality(qulity);
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T run() {
		
		Comparator<T> qualityComparator = (u1,u2)->-1*Double.compare(u1.getQuality(), u2.getQuality());
		T[] population = startPopulationGenerator.generate();
		
		EvolutionUtil.evaluate(population,parallel,evaluate);
		
		do{
			
			double maxQuality = Arrays.stream(population).mapToDouble(IClonable::getQuality).max().getAsDouble();
			int clonePopulationSize = 0;
			for(int i=0; i<population.length;i++){
				clonePopulationSize += (int) ((beta*population.length)*(population[i].getQuality()/maxQuality));
			}
			
			T[] clonePopulation = (T[]) Array.newInstance(population[0].getClass(), clonePopulationSize);
			for(int i=0, c=0; i<population.length;i++){
				int unitCloneCount = (int) ((beta*population.length)*(population[i].getQuality()/maxQuality));
				for(int j=0; j<unitCloneCount;j++){
					clonePopulation[c] = population[i].clone();
					c++;
				}
			}

			mutation.mutate(clonePopulation);
			EvolutionUtil.evaluate(clonePopulation,parallel,evaluate);
			
			Arrays.sort(clonePopulation, 0, clonePopulationSize, qualityComparator);
			T[] dPopulation = dPopulationGenerator.generate();
			EvolutionUtil.evaluate(dPopulation,parallel,evaluate);
			
			System.arraycopy(clonePopulation, 0, population, 0, population.length);
			System.arraycopy(dPopulation, 0, population, population.length-dPopulation.length, dPopulation.length);
			
		}while(limmiter.shouldContinue(population));
		
		return population[0];
	}
	

}
