package evolutionary.generationCrator.implementations;

import java.lang.reflect.Array;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import evolutionary.crossing.ICrossing;
import evolutionary.generationCrator.INewGenerationCreator;
import evolutionary.mutation.IMutation;
import evolutionary.selection.ISelection;
import optimization.utility.OptimizationAlgorithmsUtility;

public class CanonicalGenerationCreator<T> implements INewGenerationCreator<T>{

	private final @Nonnull ISelection<T> selection;
	private final @Nonnull ICrossing<T> crossing;
	private final @Nonnull IMutation<T> mutation;
	private boolean elitists = false;
	private boolean allowDuplicates = true;
	private @Nonnegative int duplicateRemovalMaximumTryCount = 100;
	
	public CanonicalGenerationCreator(@Nonnull ISelection<T> selection,@Nonnull ICrossing<T> crossing,@Nonnull IMutation<T> mutation) {
		this.selection = selection;
		this.crossing = crossing;
		this.mutation = mutation;
	}

	@Override
	public T[] createNewGenerationFrom(T[] population, double[] populationFunctionValues,
			double[] populationFitnesses) {
		
		@SuppressWarnings("unchecked")
		T[] newGeneration = (T[]) Array.newInstance(population[0].getClass(), population.length);
		createNewGenerationFrom(population, newGeneration, populationFunctionValues, populationFitnesses);
		return newGeneration;
		
	}

	@Override
	public void createNewGenerationFrom(T[] population, T[] newGenerationHolder, double[] populationFunctionValues,
			double[] populationFitnesses) {
		
		boolean duplicateRemovalFailed = false;
		int duplicateRemovalTryCount = 0;
		
		int newGenerationCount = 0;
		
		if(elitists){
			newGenerationHolder[0] = OptimizationAlgorithmsUtility.getBestSolution(population, populationFitnesses);
			newGenerationCount++;
		}
		
		do{
		
			T[] parents = selection.selectFrom(population, populationFitnesses);
			T[] children = crossing.cross(parents);
			mutation.mutate(children);
			
			if(!allowDuplicates){
				children = OptimizationAlgorithmsUtility.removeDuplicates(children,newGenerationHolder, newGenerationCount);
				if(children.length ==0){
					duplicateRemovalFailed = true;
					duplicateRemovalTryCount++;
					if(duplicateRemovalTryCount>duplicateRemovalMaximumTryCount)
						allowDuplicates = true;
				}
			}
			
			int newChildrenToAccept = Math.min(children.length, population.length-newGenerationCount);
			
			System.arraycopy(children, 0, newGenerationHolder, newGenerationCount, newChildrenToAccept);
			newGenerationCount += newChildrenToAccept;
			
		}while(newGenerationCount<population.length);
	
		if(duplicateRemovalFailed)
			allowDuplicates = false;
	}

	//==========================================================================================================================
	//Algorithm behavior control
	
	@Override
	public void setElitists(boolean elitists) {
		this.elitists = elitists;
	}
	
	@Override
	public void setAllowDuplicates(boolean allowDuplicates) {
		this.allowDuplicates = allowDuplicates;
	}
	
	@Override
	public void setDuplicateRemovalMaximumTryCount(@Nonnegative int duplicateRemovalMaximumTryCount) {
		this.duplicateRemovalMaximumTryCount = duplicateRemovalMaximumTryCount;
	}
	
}
