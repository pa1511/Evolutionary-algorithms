package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.BitVectorSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class SimpleVectorBreakCrossing extends AbstractCrossing<BitVectorSolution>{

	private final @Nonnull Function<BitVectorSolution[], Function<BitVectorSolution[],IntConsumer>> simpleVectorBreakCrossing;
	
	public SimpleVectorBreakCrossing() {
		simpleVectorBreakCrossing = chromosomes -> newChromosomesHolder -> i -> {
			Random random = RNGProvider.getRandom();

			boolean[] fathersGenes = chromosomes[2*i].getActualEncoding();
			boolean[] mothersGenes = chromosomes[2*i+1].getActualEncoding();
			
			if(fathersGenes.length!=mothersGenes.length)
				throw new IllegalArgumentException("Father and mother should have the same number of geens");
			
			int splitByte = random.nextInt(fathersGenes.length);
			
			boolean[] child1Geens = new boolean[fathersGenes.length];
			boolean[] child2Geens = new boolean[fathersGenes.length];
			
			System.arraycopy(fathersGenes, 0, child1Geens, 0, splitByte);
			System.arraycopy(mothersGenes, 0, child2Geens, 0, splitByte);
			
			System.arraycopy(mothersGenes, splitByte, child1Geens, splitByte, fathersGenes.length-splitByte);
			System.arraycopy(fathersGenes, splitByte, child2Geens, splitByte, fathersGenes.length-splitByte);
			
			newChromosomesHolder[2*i] = new BitVectorSolution(child1Geens);
			newChromosomesHolder[2*i+1] = new BitVectorSolution(child2Geens);
		};
	}
		
	@Override
	public void cross(@Nonnull BitVectorSolution[] chromosomes,@Nonnull BitVectorSolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length/2, parallel,simpleVectorBreakCrossing.apply(chromosomes).apply(newChromosomesHolder));
	}
	
	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize%2==0 ? populationSize : populationSize-1;
	}

}
