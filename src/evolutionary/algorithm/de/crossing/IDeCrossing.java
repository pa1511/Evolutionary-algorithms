package evolutionary.algorithm.de.crossing;

import javax.annotation.Nonnull;

import evolutionary.algorithm.de.DE;


/**
 * {@link DE} algorithm target and mutant crossing strategy
 *
 * @param <T>
 */
public interface IDeCrossing<T> {

	/**
	 * Crosses the target and the mutant producing the trial.
	 * @param target
	 * @param mutant
	 * @return
	 */
	public T cross(@Nonnull T target,@Nonnull T mutant);
	
}
