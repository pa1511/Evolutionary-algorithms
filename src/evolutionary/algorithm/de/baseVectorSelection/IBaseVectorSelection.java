package evolutionary.algorithm.de.baseVectorSelection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import evolutionary.algorithm.de.DE;

/**
 * {@link DE} evolutionary algorithm base vector selection strategy interface. <br>
 *
 * @param <T>
 */
public interface IBaseVectorSelection<T> {

	/**
	 * Returns the index of the unit in the population which should serve as the base vector. <br>
	 */
	public int select(@Nonnegative int targetIndex,@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull double[] populationFunctionValues);
}
