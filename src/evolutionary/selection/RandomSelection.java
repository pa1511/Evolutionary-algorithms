package evolutionary.selection;

import java.util.Random;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class RandomSelection<T> extends AbstractSelection<T>{
	
	public RandomSelection(@Nonnegative int numberOfParentsToSelect) {
		super(numberOfParentsToSelect);
	}

	@Override
	public void selectFrom(@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull T[] selectedChromosomesHolder) {
		PStreams.forEachIndexIn(numberOfChromosomesToSelect, parallel,i->{
			Random random = RNGProvider.getRandom();
			selectedChromosomesHolder[i]  = population[random.nextInt(population.length)];
		});
	}
}
