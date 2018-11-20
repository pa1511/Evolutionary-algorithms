package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.IntegerArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class SwapMutation extends AbstractMutation<IntegerArraySolution>{

	private final @Nonnull Function<IntegerArraySolution[], IntConsumer> swapMutation;
	private final @Nonnull Consumer<IntegerArraySolution> swapMutationPerUnit;
	
	public SwapMutation(double mutationChance) {
		super(mutationChance);
		swapMutationPerUnit = unit -> {
			Random random = RNGProvider.getRandom();
			
			int[] order = unit.values;
			for(int i=0; i<order.length;i++){
				if(random.nextDouble()<=mutationChance){
					int position = random.nextInt(order.length);
					
					int swapHelp = order[i];
					order[i] = order[position];
					order[position] = swapHelp;
				}					
			}
		};
		swapMutation = solutions -> k -> {
			swapMutationPerUnit.accept(solutions[k]);
		};
	}
	
	@Override
	public void mutate(@Nonnull IntegerArraySolution[] solutions) {
		PStreams.forEachIndexIn(solutions.length, parallel, swapMutation.apply(solutions));
	}
	
	@Override
	public void mutate(IntegerArraySolution child) {
		swapMutationPerUnit.accept(child);
	}

}
