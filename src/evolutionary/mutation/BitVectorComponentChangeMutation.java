package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.BitVectorSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class BitVectorComponentChangeMutation extends AbstractMutation<BitVectorSolution>{

	private final @Nonnull Function<BitVectorSolution[],IntConsumer> bitVectorComponentChangeMutation;
	private final @Nonnull Consumer<BitVectorSolution> bitVectorPerUnitComponentChangeMutation;

	public BitVectorComponentChangeMutation(double mutationChance) {
		super(mutationChance);
		bitVectorPerUnitComponentChangeMutation = child ->{
			Random random = RNGProvider.getRandom();
			
			boolean[] encoding = child.getActualEncoding();
			for(int j=0; j<encoding.length;j++){
				encoding[j] = encoding[j] ^ random.nextDouble()<=mutationChance;
			}
		};
		bitVectorComponentChangeMutation = children -> i -> {
			bitVectorPerUnitComponentChangeMutation.accept(children[i]);
		};
	}
	
	@Override
	public void mutate(BitVectorSolution[] children) {
		PStreams.forEachIndexIn(children.length, parallel, i->bitVectorComponentChangeMutation.apply(children));
	}
	
	@Override
	public void mutate(BitVectorSolution child) {
		bitVectorPerUnitComponentChangeMutation.accept(child);
	}

}
