package evolutionary.algorithm.de.crossing;

import java.util.Random;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import evolutionary.algorithm.de.DE;
import optimization.solution.DoubleArraySolution;
import utilities.random.RNGProvider;

/**
 * {@link DE} algorithm exponential crossing strategy. <br>
 * It makes sure at least one component is taken from the mutant vector. <br>
 * After copying that one it goes component by component and checks whether to copy from the mutant vector. <br>
 * After this check fails all other components are copied from the target vector.
 *
 */
public class DeExponentialDoubleArrayCrossing implements IDeCrossing<DoubleArraySolution>{

	private final @Nonnegative double crossingChance;

	public DeExponentialDoubleArrayCrossing(@Nonnegative double crossingChance) {
		this.crossingChance = crossingChance;
	}
	
	@Override
	public @Nonnull DoubleArraySolution cross(@Nonnull DoubleArraySolution target,@Nonnull DoubleArraySolution mutant) {
		
		Random random = RNGProvider.getRandom();
		
		double[] trialAsArray = new double[target.values.length];
				
		int start = random.nextInt(target.values.length);
		trialAsArray[start] = mutant.values[start];
		boolean passed = true;
		
		int i=(start+1)%trialAsArray.length;
		do{
			
			if(passed && crossingChance<random.nextDouble()){
				trialAsArray[i] = mutant.values[i];
			}
			else{
				trialAsArray[i] = target.values[i];
				passed = false;
			}
			
			i++;
			i = (i+trialAsArray.length)%trialAsArray.length;
		}while(i!=start);
					
		return new DoubleArraySolution(trialAsArray);
	}

}
