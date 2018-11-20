package evolutionary.crossing;

import java.lang.reflect.Array;

import javax.annotation.Nonnull;

import utilities.streamAndParallelization.AbstractParallelizable;

public abstract class AbstractCrossing<T> extends AbstractParallelizable implements ICrossing<T> {
	
	@Override
	public final @Nonnull T[] cross(@Nonnull T[] chromosomes) {
		@SuppressWarnings("unchecked")
		T[] newChromosomes = (T[]) Array.newInstance(chromosomes[0].getClass(), getNumberOfChromosomesProducedForPopulationSize(chromosomes.length));
		cross(chromosomes, newChromosomes);
		return newChromosomes;
	}
		
}
