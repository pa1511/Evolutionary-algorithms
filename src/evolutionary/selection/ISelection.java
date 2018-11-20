package evolutionary.selection;

import java.lang.reflect.Array;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import utilities.streamAndParallelization.IParallelizable;

/**
 * Evolutionary algorithm chromosome selection. <br>
 * It is a strategy to select chromosomes from a given population. 
 */
public interface ISelection<T> extends IParallelizable {
	
	/**
	 * Selects from the given population and returns the selected chromosomes
	 */
	public default T[] selectFrom(@Nonnull T[] population,@Nonnull double[] populationFitness) {
		@SuppressWarnings("unchecked")
		T[] selectedParents = (T[]) Array.newInstance(population[0].getClass(), getNumberOfChromosomesToSelect());
		selectFrom(population, populationFitness, selectedParents);
		return selectedParents;
	}
	
	/**
	 * Selects from the given population and stores the selected chromosomes in the given selectedChromosomesHolder
	 */
	public void selectFrom(@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull T[] selectedChromosomesHolder);
	
	/**
	 * Sets the number of chromosomes that should be selected by the strategy
	 */
	public void setNumberOfChromosomesToSelect(@Nonnegative int numberOfParentsToSelect);
	
	/**
	 * Number of chromosomes the strategy will select
	 */
	public int getNumberOfChromosomesToSelect();
	
}
