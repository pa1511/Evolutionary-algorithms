package evolutionary.crossing;

import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.DoubleArraySolution;
import utilities.streamAndParallelization.PStreams;

public class LinearCrossing extends AbstractCrossing<DoubleArraySolution>{

	private final @Nonnull Function<DoubleArraySolution[], Function<DoubleArraySolution[],IntConsumer>> linearCrossing; 
	
	public LinearCrossing() {
		
		linearCrossing = chromosomes -> newChromosomesHolder -> j-> {
			
			double[] parent1Values = chromosomes[2*j].values;
			double[] parent2Values = chromosomes[2*j+1].values;
			
			double[] child1 = new double[parent1Values.length];
			double[] child2 = new double[parent1Values.length];
			double[] child3 = new double[parent1Values.length];
			
			for(int i=0; i<parent1Values.length;i++){
				child1[i] = (parent1Values[i] + parent2Values[i])/2;
				child2[i] = 1.5*parent1Values[i] - 0.5*parent2Values[i];
				child3[i] = -0.5*parent1Values[i] + 1.5*parent2Values[i];
			}
			
			newChromosomesHolder[3*j] = new DoubleArraySolution(child1);
			newChromosomesHolder[3*j+1] = new DoubleArraySolution(child2);
			newChromosomesHolder[3*j+2] = new DoubleArraySolution(child3);
		};
	}
	
	@Override
	public void cross(DoubleArraySolution[] chromosomes, DoubleArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(chromosomes.length/2, parallel, linearCrossing.apply(chromosomes).apply(newChromosomesHolder));
	}

	@Override
	public int getNumberOfChromosomesProducedForPopulationSize(int populationSize) {
		return 3*(populationSize/2);
	}

}
