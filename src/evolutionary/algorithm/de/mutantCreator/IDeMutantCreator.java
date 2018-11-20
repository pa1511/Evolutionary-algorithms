package evolutionary.algorithm.de.mutantCreator;

import javax.annotation.Nonnull;

import evolutionary.algorithm.de.DE;

/**
 * {@link DE} evolutionary algorithm mutant creation strategy. <br>
 * Implementations of this strategy will combine the provided linearCombinations and the base vector into a mutant vector. <br>
 *
 * @param <T>
 */
public interface IDeMutantCreator<T> {

	public T createFrom(@Nonnull T base, @Nonnull T[] linearCombinations);
}
