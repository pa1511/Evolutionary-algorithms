package evolutionary.crossing;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.IntegerArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;


public class OX1Crossing extends AbstractCrossing<IntegerArraySolution> {

	private final Function<IntegerArraySolution[], Function<IntegerArraySolution[],IntConsumer>> ox1Crossing;
	
	public OX1Crossing() {
		ox1Crossing = chromosomes -> newChromosomesHolder -> j -> {
			
			Random random = RNGProvider.getRandom();
			
			int[] parent1Content = chromosomes[2*j].values;
			int[] parent2Content = chromosomes[(2*j+1)%chromosomes.length].values;
			
			final int size = parent1Content.length;
	
			int number1 = random.nextInt(size - 1);
			int number2 = random.nextInt(size);
	
			if(number1>number2){
				int swap = number1;
				number1 = number2;
				number2 = swap;
			}
			
	
			int[] child1Content = new int[size];
			int childContentCurrentIndex = 0;
			
		 	for(int stick:Arrays.copyOfRange(parent1Content, number1, number2)){
		 		child1Content[childContentCurrentIndex] = stick;
		 		childContentCurrentIndex++;
		 	}
			
			int currentStickIndex = 0;
			for (int i = 0; i<size;i++) {
	
				currentStickIndex = (number2 + i) % size;
	
				final int currentStickInParent2Content = parent2Content[currentStickIndex];
	
				if (!Arrays.stream(child1Content).anyMatch(v->v==currentStickInParent2Content)) {
					child1Content[childContentCurrentIndex]= currentStickInParent2Content;
					childContentCurrentIndex++;
				}
	
			}
	
			rotate(child1Content, number1);
			
			newChromosomesHolder[j] = new IntegerArraySolution(child1Content);

		};
	}
	
	@Override
	public void cross(@Nonnull IntegerArraySolution[] chromosomes,@Nonnull IntegerArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel,ox1Crossing.apply(chromosomes).apply(newChromosomesHolder));
	}

	private static void rotate(@Nonnull int[] content, @Nonnegative int shift) {
	    int offset = content.length - shift % content.length;
	    if (offset > 0) {
	        int[] copy = content.clone();
	        for (int i = 0; i < content.length; ++i) {
	            int j = (i + offset) % content.length;
	            content[i] = copy[j];
	        }
	    }
	}

	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;
	}

}
