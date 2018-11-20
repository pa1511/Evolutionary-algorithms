package evolutionary.crossing;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import optimization.solution.IntegerArraySolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;


public class OX2Crossing extends AbstractCrossing<IntegerArraySolution>{
	
	private final @Nonnull Function<IntegerArraySolution[], Function<IntegerArraySolution[],IntConsumer>> ox2Crossing;
	
	public OX2Crossing(@Nonnegative int selectionCount) {
		ox2Crossing = chromosomes -> newChromosomesHolder -> j -> {
			Random random = RNGProvider.getRandom();

			int[] parent1Content = chromosomes[2*j].values;
			int[] parent2Content = chromosomes[(2*j+1)%chromosomes.length].values;
	
			int[] selectedIndexes = new int[selectionCount];
			for(int i=0; i<selectedIndexes.length;i++){
				int selected = random.nextInt(92131)%parent1Content.length;
				boolean isAllreadySelected = false;
				for(int k=0; k<i;k++){
					if(selectedIndexes[k]==selected){
						isAllreadySelected = true;
						break;
					}
				}
				
				if(isAllreadySelected)
					i--;
				else{
					selectedIndexes[i] = selected;
				}
			}
			
			int[] child1Content = createChildContent(parent1Content, parent2Content, selectedIndexes);
			IntegerArraySolution solution = new IntegerArraySolution(child1Content);
			newChromosomesHolder[j] = solution;
		};
	}

	@Override
	public void cross(@Nonnull IntegerArraySolution[] chromosomes,@Nonnull IntegerArraySolution[] newChromosomesHolder) {
		PStreams.forEachIndexIn(newChromosomesHolder.length, parallel,j->ox2Crossing.apply(chromosomes).apply(newChromosomesHolder));
	}

	private @Nonnull int[] createChildContent(@Nonnull int[] parent1Content,@Nonnull  int[] parent2Content,@Nonnull  int[] selectedIndexes) {
		
		int[] childContent = new int[parent1Content.length];
		
		int selectedToTake = 0;
		for(int i=0; i<childContent.length;i++){
			if(chromosomeInOtherParentOnSelectedIndex(parent1Content[i],parent2Content,selectedIndexes)){
				childContent[i] = parent2Content[selectedIndexes[selectedToTake]];
				selectedToTake++;
			}
			else{
				childContent[i] = parent1Content[i];
			}
			
		}
		
		return childContent;
	}
	
	
	private boolean chromosomeInOtherParentOnSelectedIndex(int value,@Nonnull int[] parent2Content,@Nonnull int[] selectedIndexes) {
		for(int i=0; i<selectedIndexes.length;i++){
			if(value==parent2Content[selectedIndexes[i]])
				return true;
		}
		return false;
	}

	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		return populationSize/2;
	}
}
