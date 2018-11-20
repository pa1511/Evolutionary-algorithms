package evolutionary.generationCrator.implementations;

import javax.annotation.Nonnull;

import evolutionary.crossing.ICrossing;
import evolutionary.generationCrator.INewGenerationCreator;
import evolutionary.mutation.IMutation;
import evolutionary.selection.ISelection;
import optimization.utility.OptimizationAlgorithmsUtility;

public class SimpleGenerationCreator<T> implements INewGenerationCreator<T>{
	
	private final @Nonnull ISelection<T> selection;
	private final @Nonnull ICrossing<T> crossing;
	private final @Nonnull IMutation<T> mutation;
	private boolean elitists;
	
	public SimpleGenerationCreator(@Nonnull ISelection<T> selection,@Nonnull ICrossing<T> crossing,@Nonnull IMutation<T> mutation) {
		this.selection = selection;
		this.crossing = crossing;
		this.mutation = mutation;
		
		selection.getNumberOfChromosomesToSelect();
	}

	@Override
	public T[] createNewGenerationFrom(T[] population, double[] populationFunctionValues,
			double[] populationFitnesses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createNewGenerationFrom(T[] population, T[] newGenerationHolder, double[] populationFunctionValues,
			double[] populationFitnesses) {
		
		T best = null;
		if(elitists){
			best = OptimizationAlgorithmsUtility.getBestSolution(population, populationFitnesses);
		}
		
		this.selection.setNumberOfChromosomesToSelect(newGenerationHolder.length);
		selection.selectFrom(population, populationFitnesses, population);
		crossing.cross(population, newGenerationHolder);
		mutation.mutate(newGenerationHolder);
		
		if(elitists){
			newGenerationHolder[0] = best;
		}
		
	}

	@Override
	public void setElitists(boolean elitists) {
		this.elitists = elitists;
	}

	@Override
	public void setAllowDuplicates(boolean allowDuplicates) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDuplicateRemovalMaximumTryCount(int duplicateRemovalMaximumTryCount) {
		// TODO Auto-generated method stub
		
	}

}
