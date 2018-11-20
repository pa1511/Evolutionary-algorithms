package evolutionary.crossing;

import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import utilities.streamAndParallelization.PStreams;

public class WholeArithmeticRecombination extends AbstractCrossing<DoubleArraySolution>{
		
	private final @Nonnull Function<DoubleArraySolution[], Function<DoubleArraySolution[],IntConsumer>> wholeArithmeticRecombination = chromosomes -> newChromosomesholder -> i -> {
		double[] father = chromosomes[2*i%chromosomes.length].values;
		double[] mother = chromosomes[(2*i+1)%chromosomes.length].values;
		
		double[] child;
		if(newChromosomesholder[i]!=null){
			child = newChromosomesholder[i].values;
		}
		else{
			child = new double[father.length];
			newChromosomesholder[i] = new DoubleArraySolution(child);
		}
		
		for(int j=0; j<child.length; j++){
			child[j] = (father[j]+mother[j])/2;
		}
		
	};
	
	@Override
	public void cross(@Nonnull DoubleArraySolution[] chromosomes,@Nonnull DoubleArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel, wholeArithmeticRecombination.apply(chromosomes).apply(newChromosomesHolder));
	}

	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;
	}

}
