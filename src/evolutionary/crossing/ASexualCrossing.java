package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.SingleObjectiveSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class ASexualCrossing<T extends SingleObjectiveSolution<T>> extends AbstractCrossing<T>{

	private final @Nonnull Function<Random,Function<T[], Function<T[],IntConsumer>>> asexualCross;

	public ASexualCrossing() {
		asexualCross = random -> chromosomes -> newChromosomesHolder -> i -> {
			
			T father = chromosomes[2*i%chromosomes.length];
			
			if(newChromosomesHolder[i]==null){
				newChromosomesHolder[i] = father.clone();
			}
			else{
				newChromosomesHolder[i].copy(father);
			}
		};
	}

	@Override
	public void cross(@Nonnull T[] chromosomes,@Nonnull T[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel,asexualCross.apply(RNGProvider.getRandom()).apply(chromosomes).apply(newChromosomesHolder));
	}
	
	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;//populationSize%2==0 ? populationSize : populationSize-1;
	}
}
