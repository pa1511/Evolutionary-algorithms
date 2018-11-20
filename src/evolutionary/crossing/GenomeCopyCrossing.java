package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.BitVectorSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class GenomeCopyCrossing extends AbstractCrossing<BitVectorSolution>{

	private final @Nonnull Function<BitVectorSolution[], Function<BitVectorSolution[],IntConsumer>> genomeCopyCross;
	
	/**
	 * Mistake chance 0.01
	 */
	public GenomeCopyCrossing() {
		this(0.01);
	}
	
	public GenomeCopyCrossing(@Nonnegative double mistakeChance) {
		genomeCopyCross = chromosomes -> newChromosomesHolder -> i ->{
			
			Random random = RNGProvider.getRandom();
			
			boolean[] father = chromosomes[2*i].getActualEncoding();
			boolean[] mother = chromosomes[2*i+1].getActualEncoding();
			
			boolean[] child = new boolean[father.length];
			
			for(int j=0; j<father.length;j++){
				
				if(father[j]==mother[j]){
					child[j] = father[j] ^ mistakeChance>random.nextDouble();
				}
				else{
					child[j] = i%2==0;
				}
			}
			
			newChromosomesHolder[i] = new BitVectorSolution(child);
		};
	}
	
	@Override
	public void cross(@Nonnull BitVectorSolution[] chromosomes,@Nonnull BitVectorSolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel, genomeCopyCross.apply(chromosomes).apply(newChromosomesHolder));
	}

	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;
	}

}
