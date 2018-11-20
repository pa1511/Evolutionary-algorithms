package evolutionary.algorithm.de.linearCombinationProvider;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import optimization.utility.OptimizationAlgorithmsUtility;
import util.VectorUtils;

/**
 * This {@link IDeLinearCombinationProvider} implementation makes sure that one linear combination is the combination of the currently best vector in the population and the current target vector. <br>
 *
 */
public class TargetToBesDoubleArraytLinearCombinationProvider implements IDeLinearCombinationProvider<DoubleArraySolution>{

	private final @Nonnull RandomLinearCombinationProvider linearCombinationProvider;

	public TargetToBesDoubleArraytLinearCombinationProvider(@Nonnegative int numberOfOtherLinearCombinationsToSelect) {
		linearCombinationProvider = new RandomLinearCombinationProvider(1+numberOfOtherLinearCombinationsToSelect);;
	}
	
	
	@Override
	public DoubleArraySolution[] provideFrom(@Nonnull DoubleArraySolution[] population,@Nonnull double[] populationFitness,
			@Nonnull double[] populationFunctionValues,@Nonnegative int baseIndex) {
		
		DoubleArraySolution[] linearCombinations = linearCombinationProvider.provideFrom(population, populationFitness, populationFunctionValues, baseIndex);
		
		int bestIndex = OptimizationAlgorithmsUtility.getBestSolutionIndex(population, populationFitness);
		double[] bestAsVector = population[bestIndex].values;
		double[] baseAsVector = population[baseIndex].values;
		double[] linearCombinationArray = VectorUtils.subtract(bestAsVector, baseAsVector,false);
		
		linearCombinations[0] = new DoubleArraySolution(linearCombinationArray);
		return linearCombinations;
	}

}
