package evolutionary.selection;

import java.util.Arrays;
import java.util.Random;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class RouletteWheelSelection<T> extends AbstractSelection<T>{
	
	/**
	 * Will select 2 parents
	 */
	public RouletteWheelSelection() {
		this(2);
	}

	public RouletteWheelSelection(@Nonnegative int numberOfParentsToSelect) {
		super(numberOfParentsToSelect);
	}

	//==========================================================================================================================
		
	@Override
	public void selectFrom(@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull T[] selectedChromosomesHolder) {
		double fitnessSum = Arrays.stream(populationFitness).sum();
		double[] selectionChance = new double[population.length];

		for(int i=0; i<population.length;i++){
			selectionChance[i] = populationFitness[i]/fitnessSum;
		}
		
		PStreams.forEachIndexIn(numberOfChromosomesToSelect, parallel,i->{
			selectedChromosomesHolder[i] = selectParent(population, selectionChance);
		});
	}

	public @Nonnull T selectParent(@Nonnull T[] population,@Nonnull double[] selectionChance) {
		Random random = RNGProvider.getRandom();
		
		double select = random.nextDouble();		
		double sum = 0;
		
		for(int i=0; i<population.length;i++){
			sum+=selectionChance[i];
			if(sum>=select)
				return population[i];
		}

		return population[population.length-1];
	}
		
}
