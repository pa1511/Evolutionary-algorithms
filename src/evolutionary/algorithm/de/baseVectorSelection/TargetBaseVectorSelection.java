package evolutionary.algorithm.de.baseVectorSelection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import evolutionary.algorithm.de.DE;

/**
 * Base vector selection strategy to be used in the {@link DE} evolutionary algorithm which returns the current target vector as the base vector. <br>
 *
 * @param <T>
 */
public class TargetBaseVectorSelection<T> implements IBaseVectorSelection<T>{

	/**
	 * Returns the index of the current target vector
	 */
	@Override
	public int select(@Nonnegative int targetIndex,@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull double[] populationFunctionValues) {
		return targetIndex;
	}

}
