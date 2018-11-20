package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class ComponentChangeMutation extends AbstractMutation<DoubleArraySolution>{

	private final @Nonnull Function<DoubleArraySolution[], IntConsumer> componentChangeMutation;
	private final @Nonnull Consumer<DoubleArraySolution> componentChangeMutationPerUnit;
	
	public ComponentChangeMutation(double mutationChance, double min, double max) {
		super(mutationChance);
		double interval = max-min;
		componentChangeMutationPerUnit = unit -> {
			Random random = RNGProvider.getRandom();
			double[] childEncoding = unit.values;
			
			if(mutationChance>random.nextDouble()){
				childEncoding[random.nextInt(childEncoding.length)] = random.nextDouble()*interval+min;
			}
		};
		
		componentChangeMutation = children -> i -> componentChangeMutationPerUnit.accept(children[i]);
	}
	
	@Override
	public void mutate(@Nonnull DoubleArraySolution[] children) {
		PStreams.forEachIndexIn(children.length, parallel, componentChangeMutation.apply(children));
	}

	@Override
	public void mutate(DoubleArraySolution child) {
		componentChangeMutationPerUnit.accept(child);
	}
}
