package evolutionary.crossing;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import utilities.streamAndParallelization.IParallelizable;

/**
 * Evolutionary algorithm crossing strategy <br>
 * Crossing creates new chromosomes using the given chromosomes and combining them in some way. 
 */
public interface ICrossing<T> extends IParallelizable {

	public void cross( @Nonnull T[] chromosomes, @Nonnull T[] newChromosomesHolder);
	public @Nonnull T[] cross( @Nonnull T[] chromosomes);

	/**
	 * Returns a number of chromosomes that will be created if the given population is of populationSize
	 */
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize);
}
