package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.BitVectorSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class SparseBitVectorMutation extends AbstractMutation<BitVectorSolution>{

	private final @Nonnull Function<BitVectorSolution[],IntConsumer> sparseBitVectorMutation;
	private final @Nonnull Consumer<BitVectorSolution> perUnitSparseBitVectorMutation;
	
	public SparseBitVectorMutation(@Nonnegative double mutationChance) {
		super(mutationChance);
		perUnitSparseBitVectorMutation = unit -> {
			Random random = RNGProvider.getRandom();
			
			if(mutationChance<random.nextDouble()){
				boolean[] encoding = unit.getActualEncoding();
				int mutationPosition = random.nextInt(encoding.length);
				encoding[mutationPosition] = !encoding[mutationPosition];
			}
		};
		sparseBitVectorMutation = children -> i -> perUnitSparseBitVectorMutation.accept(children[i]);
	}

	@Override
	public void mutate(BitVectorSolution[] children) {
		PStreams.forEachIndexIn(children.length, parallel, sparseBitVectorMutation.apply(children));
	}

	@Override
	public void mutate(BitVectorSolution child) {
		perUnitSparseBitVectorMutation.accept(child);
	}

}
