package evolutionary.generationCrator;

import javax.annotation.Nonnegative;

public interface INewGenerationCreator<T> {

	public T[] createNewGenerationFrom(T[] population, double[] populationFunctionValues, double[] populationFitnesses);
	public void createNewGenerationFrom(T[] population, T[] newGenerationHolder, double[] populationFunctionValues, double[] populationFitnesses);
	public void setElitists(boolean elitists);
	public void setAllowDuplicates(boolean allowDuplicates);
	public void setDuplicateRemovalMaximumTryCount(@Nonnegative int duplicateRemovalMaximumTryCount);
}
