package evolutionary.util;

import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnegative;

import optimization.decoder.IDecoder;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import utilities.streamAndParallelization.PStreams;

public class EvolutionUtil {

	private EvolutionUtil() {}

	public static<T> void evaluate(@Nonnegative T[] population, boolean parallel, Function<T[],IntConsumer> evaluator) {
		PStreams.forEachIndexIn(population.length, parallel, evaluator.apply(population));
	}

	public static<I,T> void evaluatePerChromosome(T[] population, double[] populationFunctionValues, double[] populationFitnessValues,
			ToDoubleFunction<I> function, IDecoder<T,I> decoder, IFitnessEvaluator<T> evaluator, boolean parallel){
		
		PStreams.forEachIndexIn(population.length, parallel,(i)->{
			populationFunctionValues[i] = function.applyAsDouble(decoder.decode(population[i]));
			populationFitnessValues[i] = evaluator.evaluate(population[i], populationFunctionValues[i]);
		});		
	}
	
	
}
