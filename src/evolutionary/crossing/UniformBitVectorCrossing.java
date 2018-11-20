package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.BitVectorSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class UniformBitVectorCrossing extends AbstractCrossing<BitVectorSolution>{

	private final @Nonnull Function<BitVectorSolution[], Function<BitVectorSolution[],IntConsumer>> uniformBitVectorCrossing;
	
	public UniformBitVectorCrossing() {
		uniformBitVectorCrossing = chromosomes -> newChromosomesHolder -> i -> {
			Random random = RNGProvider.getRandom();

			boolean[] father = chromosomes[2*i].getActualEncoding();
			boolean[] mother = chromosomes[2*i+1].getActualEncoding();
			
			boolean[] child = new boolean[father.length];
			
			for(int j=0; j<child.length; j++){
				child[j] = (father[j] && mother[j]) || (random.nextBoolean() && (father[j] ^ mother[j]));
			}
	
			newChromosomesHolder[i] = new BitVectorSolution(child);
		};
	}
	
	@Override
	public void cross(@Nonnull BitVectorSolution[] chromosomes,@Nonnull BitVectorSolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel, i->uniformBitVectorCrossing.apply(chromosomes).apply(newChromosomesHolder));
	}

	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;
	}

}
