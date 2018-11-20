package evolutionary.crossing;

import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import optimization.utility.OptimizationAlgorithmsUtility;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class DoubleArrayOperatorSwitchCrossing extends AbstractCrossing<DoubleArraySolution>{

	private final @Nonnull Function<DoubleArraySolution[], Function<DoubleArraySolution[], IntConsumer>> doubleArrayOperatorSwitchCrossing;
	
	public DoubleArrayOperatorSwitchCrossing(@Nonnull DoubleBinaryOperator[] operators, double min, double max) {
		doubleArrayOperatorSwitchCrossing = chromosomes -> newChromosomesHolder -> i -> {
			
			Random random = RNGProvider.getRandom();
			
			double[] father = chromosomes[2*i].values;
			double[] mother = chromosomes[2*i+1].values;
			
			double[] child = new double[father.length];
			for(int j=0; j<child.length; j++){
				child[j] = operators[random.nextInt(operators.length)].applyAsDouble(father[j], mother[j]);
				child[j] = OptimizationAlgorithmsUtility.placeValueInInterval(min, max, child[j]);
			}
			
			newChromosomesHolder[i] = new DoubleArraySolution(child);
		};
	}
	
	@Override
	public void cross(@Nonnull DoubleArraySolution[] chromosomes,@Nonnull DoubleArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel, doubleArrayOperatorSwitchCrossing.apply(chromosomes).apply(newChromosomesHolder));
	}

	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;
	}

}
