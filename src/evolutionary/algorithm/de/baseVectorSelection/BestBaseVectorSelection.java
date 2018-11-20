package evolutionary.algorithm.de.baseVectorSelection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.utility.OptimizationAlgorithmsUtility;

/**
 * Base vector selection strategy which selects the best unit in the population as the base vector. <br>
 * 
 * @param <T>
 */
public class BestBaseVectorSelection<T> implements IBaseVectorSelection<T>{

	/**
	 * Returns the index of the best unit in the population to serve as the base vector
	 */
	@Override
	public int select(@Nonnegative int targetIndex,@Nonnull T[] population,@Nonnull  double[] populationFitness,@Nonnull double[] populationFunctionValues) {
		return OptimizationAlgorithmsUtility.getBestSolutionIndex(population, populationFitness);
	}

}
