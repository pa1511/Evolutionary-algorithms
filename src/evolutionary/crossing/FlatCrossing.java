package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class FlatCrossing extends AbstractCrossing<DoubleArraySolution>{

	private final @Nonnull Function<DoubleArraySolution[], Function<DoubleArraySolution[],IntConsumer>> flatCross;
		
	public FlatCrossing() {
		flatCross = chromosomes -> newChromosomesHolder -> i -> {
			Random random = RNGProvider.getRandom();
			double[] father = chromosomes[2*i].values;
			double[] mother = chromosomes[2*i+1].values;
			
			double[] child = new double[father.length];
			for(int j=0; j<child.length;j++){
				double interval = Math.abs(father[j]-mother[j]);
				double min = Math.min(father[j], mother[j]);
				
				child[j] = interval*random.nextDouble()+min;
			}
			
			newChromosomesHolder[i] = new DoubleArraySolution(child);
		};
	}

	@Override
	public void cross(DoubleArraySolution[] chromosomes, DoubleArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel, flatCross.apply(chromosomes).apply(newChromosomesHolder));
	}

	@Override
	public int getNumberOfChromosomesProducedForPopulationSize(int populationSize) {
		return populationSize/2;
	}

}
