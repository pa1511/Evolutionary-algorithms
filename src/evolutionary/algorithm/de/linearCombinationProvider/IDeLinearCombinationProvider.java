package evolutionary.algorithm.de.linearCombinationProvider;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import evolutionary.algorithm.de.DE;

/**
 * Linear combination creator for the {@link DE} evolutionary algorithm <br>
 * It selects some of the current vectors in the population and creates linear combinations from them. <br>
 *
 * @param <T>
 */
public interface IDeLinearCombinationProvider<T> {
	
	public @Nonnull T[] provideFrom(@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull double[] populationFunctionValues,@Nonnegative int baseIndex);
	
}
