package evolutionary.algorithm.de.linearCombinationProvider;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import util.VectorUtils;

/**
 * This implementation of {@link IDeLinearCombinationProvider} randomly selects vectors from the current population and creates linear combinations from them. <br>
 *
 */
public class RandomLinearCombinationProvider implements IDeLinearCombinationProvider<DoubleArraySolution>{

	private final @Nonnegative int numberOfLinearCombinationsToProvide;

	public RandomLinearCombinationProvider(@Nonnegative int numberOfLinearCombinationsToProvide) {
		this.numberOfLinearCombinationsToProvide = numberOfLinearCombinationsToProvide;
	}
	
	@Override
	public DoubleArraySolution[] provideFrom(@Nonnull DoubleArraySolution[] population,@Nonnull double[] populationFitness,
			@Nonnull double[] populationFunctionValues,@Nonnegative int baseIndex) {
		DoubleArraySolution[] linearCombinations = new DoubleArraySolution[numberOfLinearCombinationsToProvide];
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		for(int i=0; i<linearCombinations.length;i++){
			int r1Index;
			do{
				r1Index = random.nextInt(population.length);
			}while(r1Index==baseIndex);
			
			int r2Index;
			do{
				r2Index = random.nextInt(population.length);
			}while(r2Index==baseIndex || r2Index==r1Index);
				
			double[] r1AsVector = population[r1Index].values;
			double[] r2AsVector = population[r2Index].values;
			double[] linearCombinationArray = VectorUtils.subtract(r1AsVector, r2AsVector,false);
			
			linearCombinations[i] = new DoubleArraySolution(linearCombinationArray);
		}
		
		return linearCombinations;
	}

}
