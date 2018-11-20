package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.IntegerArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class DiscreteIntegerCrossing extends AbstractCrossing<IntegerArraySolution>{

	private final @Nonnull Function<Random,Function<IntegerArraySolution[], Function<IntegerArraySolution[],IntConsumer>>> componentSelectionCross;

	public DiscreteIntegerCrossing() {
		this(0.5);
	}
	
	public DiscreteIntegerCrossing(@Nonnegative double parentOneSelectionChance) {
		componentSelectionCross = random -> chromosomes -> newChromosomesHolder -> i -> {
			
			int[] father = chromosomes[2*i%chromosomes.length].values;
			int[] mother = chromosomes[(2*i+1)%chromosomes.length].values;
			
			int[] child1;
			if(newChromosomesHolder[i]!=null){
				child1 = newChromosomesHolder[i].values;
			}
			else{
				child1 = new int[father.length];
				newChromosomesHolder[i] = new IntegerArraySolution(child1);
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
	public void cross(@Nonnull IntegerArraySolution[] chromosomes,@Nonnull IntegerArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel,componentSelectionCross.apply(RNGProvider.getRandom()).apply(chromosomes).apply(newChromosomesHolder));
	}
	
	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize%2==0 ? populationSize : populationSize-1;
	}
}
