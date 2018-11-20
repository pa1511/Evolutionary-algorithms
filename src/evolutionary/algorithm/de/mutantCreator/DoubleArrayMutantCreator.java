package evolutionary.algorithm.de.mutantCreator;

import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import util.VectorUtils;
import utilities.random.RNGProvider;

/**
 * Implementation of the {@link IDeMutantCreator} which knows how to combine the base vector and linear combinations represented by {@link DoubleArraySolution} <br>
 * @author Petar
 */
public class DoubleArrayMutantCreator implements IDeMutantCreator<DoubleArraySolution>{

	private @Nonnegative double f;
	private final @Nonnull DoubleFunction<DoubleBinaryOperator> operator = r -> (x,y)->  x+r*y;

	public DoubleArrayMutantCreator(@Nonnegative double f) {
		this.f = f;
	}
	
	@Override
	public DoubleArraySolution createFrom(@Nonnull DoubleArraySolution base,@Nonnull DoubleArraySolution[] linearCombinations) {
		
		double[] mutantAsArray = base.getValues();
		
		
		Random random = RNGProvider.getRandom();
		for(int i=0; i<linearCombinations.length;i++){
			double[] linearCombinationAsArray = linearCombinations[i].values;
			VectorUtils.binaryVectorOperation(mutantAsArray, linearCombinationAsArray, mutantAsArray, operator.apply(random.nextDouble()*f), false);
		}
		
		return new DoubleArraySolution(mutantAsArray);
	}

}
