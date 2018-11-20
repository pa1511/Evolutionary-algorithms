package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.IClonable;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;


public class ProportionalMutation<T extends IClonable<T>> extends AbstractMutation<T> {
	
	private final @Nonnull Function<T[], DoubleFunction<DoubleFunction<IntConsumer>>> proportinalMutation; 
	private final @Nonnull DoubleFunction<DoubleFunction<Consumer<T>>> perUnitProportionalMutation;
	
	public ProportionalMutation(SingleMutation<T> mutation, double fi) {
		perUnitProportionalMutation = interval -> min -> unit -> {
			Random random = RNGProvider.getRandom();
			double normalizedValue = (unit.getQuality()-min)/interval;
			if(random.nextDouble()<Math.pow(Math.E, -fi*normalizedValue)){
				mutation.mutate(unit);
			}
		};
		
		proportinalMutation = population -> interval -> min ->  i -> perUnitProportionalMutation.apply(interval).apply(min).accept(population[i]);
	}
	
	@Override
	public void mutate(T[] population){
		
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;
		
		for(int i=0; i<population.length; i++){
			double quality = population[i].getQuality();
			max = Math.max(max, quality);
			min = Math.min(min, quality);
		}
		final double interval = max-min;

		PStreams.forEachIndexIn(population.length, parallel, proportinalMutation.apply(population).apply(interval).apply(min));
	}
	
	@Override
	public void mutate(T child) {
		perUnitProportionalMutation.apply(0).apply(child.getQuality()).accept(child);
	}
}
