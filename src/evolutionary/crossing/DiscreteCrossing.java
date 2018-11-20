package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class DiscreteCrossing extends AbstractCrossing<DoubleArraySolution>{

	private final @Nonnull Function<Random,Function<DoubleArraySolution[], Function<DoubleArraySolution[],IntConsumer>>> componentSelectionCross;

	public DiscreteCrossing() {
		this(0.5);
	}
	
	public DiscreteCrossing(@Nonnegative double parentOneSelectionChance) {
		componentSelectionCross = random -> chromosomes -> newChromosomesHolder -> i -> {
			
			double[] father = chromosomes[2*i%chromosomes.length].values;
			double[] mother = chromosomes[(2*i+1)%chromosomes.length].values;
			
			double[] child1;
			if(newChromosomesHolder[i]!=null){
				child1 = newChromosomesHolder[i].values;
			}
			else{
				child1 = new double[father.length];
				newChromosomesHolder[i] = new DoubleArraySolution(child1);
			}
						
			for(int j=0; j<father.length;j++){
				if(parentOneSelectionChance<random.nextDouble()){
					child1[j] = father[j];
				}
				else{
					child1[j] = mother[j];
				}
			}
			
		};
	}

	@Override
	public void cross(@Nonnull DoubleArraySolution[] chromosomes,@Nonnull DoubleArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel,componentSelectionCross.apply(RNGProvider.getRandom()).apply(chromosomes).apply(newChromosomesHolder));
	}
	
	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize%2==0 ? populationSize : populationSize-1;
	}
}
