package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.utility.OptimizationAlgorithmsUtility;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;
import optimization.solution.DoubleArraySolution;

public class BLXAlphaCrossing extends AbstractCrossing<DoubleArraySolution>{
	
	private final double alpha;
	private final @Nonnull double[] min;
	private final @Nonnull double[] max;
	private final @Nonnull Function<DoubleArraySolution[], Function<DoubleArraySolution[],IntConsumer>> blxCross;
	private boolean limitValue;


	public BLXAlphaCrossing(double alpha) {
		this(alpha,new double[]{},new double[]{},false);
	}

	public BLXAlphaCrossing(double alpha, double minValue, double maxValue) {
		this(alpha,new double[]{minValue},new double[]{maxValue});
	}

	public BLXAlphaCrossing(double alpha,@Nonnull double[] min,@Nonnull double[] max) {
		this(alpha,min,max,true);
	}

	private BLXAlphaCrossing(double alpha,@Nonnull double[] min,@Nonnull double[] max,boolean allowAny) {
		this.alpha = alpha;
		this.min = min;
		this.max = max;
		this.limitValue = allowAny;
		
		blxCross = chromosomes -> newChromosomesHolder -> j-> {
			
			Random random = RNGProvider.getRandom();
			
			double[] parent1Values = chromosomes[2*j%chromosomes.length].values;
			double[] parent2Values = chromosomes[(2*j+1)%chromosomes.length].values;
			
			double[] childValues;
			if(newChromosomesHolder[j]!=null){
				childValues = newChromosomesHolder[j].values;
			}
			else{
				childValues = new double[parent1Values.length];
				newChromosomesHolder[j] = new DoubleArraySolution(childValues);
			}			
			
			for(int i=0; i<parent1Values.length;i++){
				double minValue = Math.min(parent1Values[i], parent2Values[i]);
				double maxValue = Math.max(parent1Values[i], parent2Values[i]);
				
				double intervalSize = maxValue-minValue;
				
				childValues[i] = createChildValue(minValue,maxValue,intervalSize,i,random);
			}
			
		
		};

	}
	
	private double createChildValue(double minValue, double maxValue, double intervalSize, int i, Random random) {
		
		double lowerBound = minValue-intervalSize*alpha;
		double upperBound = maxValue+intervalSize*alpha;
				
		double value = random.nextDouble()*(upperBound-lowerBound)+lowerBound;
		
		if(limitValue){
			double minLimit = (i<min.length) ? min[i] : min[0];
			double maxLimit = (i<max.length) ? max[i] : max[0];
			value = OptimizationAlgorithmsUtility.placeValueInInterval(minLimit, maxLimit, value);
		}
		return value;
	}
	
	@Override
	public void cross(@Nonnull DoubleArraySolution[] chromosomes,@Nonnull DoubleArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel,blxCross.apply(chromosomes).apply(newChromosomesHolder));
	}


	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;
	}

}
