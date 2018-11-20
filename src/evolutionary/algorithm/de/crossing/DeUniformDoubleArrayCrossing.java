package evolutionary.algorithm.de.crossing;

import java.util.Random;

import javax.annotation.Nonnegative;

import evolutionary.algorithm.de.DE;
import optimization.solution.DoubleArraySolution;
import utilities.random.RNGProvider;

/**
 * Uniform {@link DE} algorithm crossing strategy. <br>
 * Randomly selects a vector component from either the target or the mutant vector. <br>
 * It always makes sure there is at least one component from the mutant vector. <br>
 */
public class DeUniformDoubleArrayCrossing implements IDeCrossing<DoubleArraySolution>{
	
	private final @Nonnegative double crossingChance;

	public DeUniformDoubleArrayCrossing(@Nonnegative double crossingChance) {
		this.crossingChance = crossingChance;
	}
	
	@Override
	public DoubleArraySolution cross(DoubleArraySolution target, DoubleArraySolution mutant) {
		
		Random random = RNGProvider.getRandom();
		
		double[] trialAsArray = new double[target.values.length];
				
		int start = random.nextInt(target.values.length);
		trialAsArray[start] = mutant.values[start];
		
		int i=(start+1)%trialAsArray.length;
		do{
			
			if(crossingChance<random.nextDouble()){
				trialAsArray[i] = mutant.values[i];
			}
			else{
				trialAsArray[i] = target.values[i];
			}
			
			i++;
			i = (i+trialAsArray.length)%trialAsArray.length;
		}while(i!=start);
					
		return new DoubleArraySolution(trialAsArray);
	}
}
