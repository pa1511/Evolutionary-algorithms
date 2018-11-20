package evolutionary.mutation;

import java.util.Random;

import javax.annotation.Nonnull;

import optimization.solution.Particle;
import optimization.utility.OptimizationAlgorithmsUtility;
import utilities.random.RNGProvider;

public class RandomParticleMutation implements SingleMutation<Particle>{
	
	private final double mutationChance;
	private final double value;
	private final double sigma;

	private final double minAllowedValue;
	private final double maxAllowedValue;

	public RandomParticleMutation(double mutationChance, double sigma, double minAllowedValue, double  maxAllowedValue) {
		this(mutationChance,0,sigma,minAllowedValue,maxAllowedValue);
	}


	public RandomParticleMutation(double mutationChance, double value, double sigma, double minAllowedValue, double  maxAllowedValue) {
		this.mutationChance = mutationChance;
		this.value = value;
		this.sigma = sigma;
		this.minAllowedValue = minAllowedValue;
		this.maxAllowedValue = maxAllowedValue;
	}
	@Override
	public void mutate(@Nonnull Particle unit) {
		Random random = RNGProvider.getRandom();
		double[] particlePosition = unit.getPosition();
		for(int i=0; i<particlePosition.length;i++){
			if(random.nextDouble()<mutationChance){
				double newValue = OptimizationAlgorithmsUtility.placeValueInInterval(minAllowedValue, maxAllowedValue, 
						particlePosition[i]+random.nextGaussian()*sigma+value);
				
				particlePosition[i] = newValue;
			}
		}
	}

}
