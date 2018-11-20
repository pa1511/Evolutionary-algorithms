package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.IntegerArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class FlipMutation extends AbstractMutation<IntegerArraySolution>{

	private final @Nonnull Function<IntegerArraySolution[], IntConsumer> flipMutation;
	private final @Nonnull Consumer<IntegerArraySolution> flipMutationPerUnit;
	
	public FlipMutation(double mutationChance, int subArraymaxLength) {
		super(mutationChance);
		flipMutationPerUnit = unit -> {
			Random random = RNGProvider.getRandom();
			
			int[] order = unit.values;
			if(random.nextDouble()<=mutationChance){
				int position1 = random.nextInt(order.length);
				int position2 = Math.min(order.length, position1+random.nextInt(subArraymaxLength));
				
				while(position1<position2) {
					int swapHelp = order[position1];
					order[position1] = order[position2];
					order[position2] = swapHelp;
				}
				
			}					
		};
		flipMutation = solutions -> k -> {
			flipMutationPerUnit.accept(solutions[k]);
		};
	}
	
	@Override
	public void mutate(@Nonnull IntegerArraySolution[] solutions) {
		PStreams.forEachIndexIn(solutions.length, parallel, flipMutation.apply(solutions));
	}
	
	@Override
	public void mutate(IntegerArraySolution child) {
		flipMutationPerUnit.accept(child);
	}

}
