package evolutionary.crossing;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.BitVectorSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class MultiplePointCrossing extends AbstractCrossing<BitVectorSolution>{

	private final @Nonnegative int pointCount;
	private final @Nonnull Function<BitVectorSolution[], Function<BitVectorSolution[],IntConsumer>> multiplePointCross;

	public MultiplePointCrossing(@Nonnegative int pointCount) {
		this.pointCount = pointCount;
		multiplePointCross = chromosomes -> newChromosomesHolder -> i -> {
			Random random = RNGProvider.getRandom();
			
			boolean[] code1 = chromosomes[2*i].getActualEncoding();
			boolean[] code2 = chromosomes[2*i+1].getActualEncoding();
			
			int[] breakPoints = new int[pointCount];
			for(int j=0; j<breakPoints.length;j++){
				breakPoints[j] = random.nextInt(code1.length);
			}
			Arrays.sort(breakPoints);

			boolean[] childCode1 = new boolean[code1.length];
			boolean[] childCode2 = new boolean[code1.length];
			
			int start = 0;
			int limit = breakPoints[0];
			System.arraycopy(code1, 0, childCode1, 0, limit);
			System.arraycopy(code2, 0, childCode2, 0, limit);
			
			for(int j=1; j<=breakPoints.length;j++){
				start = breakPoints[j-1];
				if(j==breakPoints.length){
					limit = code1.length-start;
				}else{
					limit = breakPoints[j]-start;
				}
				if(j%2==0){
					System.arraycopy(code1, start, childCode1, start, limit);
					System.arraycopy(code2, start, childCode2, start, limit);
				}
				else{
					System.arraycopy(code2, start, childCode1, start, limit);
					System.arraycopy(code1, start, childCode2, start, limit);
				}
			}
			
			newChromosomesHolder[2*i] = new BitVectorSolution(childCode1);
			newChromosomesHolder[2*i+1] = new BitVectorSolution(childCode2);
		};
	}
	
	@Override
	public void cross(@Nonnull BitVectorSolution[] chromosomes,@Nonnull BitVectorSolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length/2, parallel,multiplePointCross.apply(chromosomes).apply(newChromosomesHolder));
	}

	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize%2==0 ? populationSize : populationSize-1;
	}

}
