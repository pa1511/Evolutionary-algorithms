package evolutionary.algorithm.de.baseVectorSelection;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import evolutionary.algorithm.de.DE;


/**
 * Random selects a unit which will serve as the base vector in the {@link DE} evolutionary algorithm. <br>
 * @param <T>
 */
public class RandomBaseVectorSelection<T> implements IBaseVectorSelection<T>{
	
	/**
	 * Returns the index of a randomly selected unit to serve as the base vector <br>
	 */
	@Override
	public int select(@Nonnegative int targetIndex,@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull double[] populationFunctionValues) {
		return ThreadLocalRandom.current().nextInt(population.length);
	}

}
