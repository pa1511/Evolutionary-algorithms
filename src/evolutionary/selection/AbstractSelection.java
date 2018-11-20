package evolutionary.selection;

import javax.annotation.Nonnegative;

import utilities.streamAndParallelization.AbstractParallelizable;

public abstract class AbstractSelection<T> extends AbstractParallelizable implements ISelection<T> {
	
	protected @Nonnegative int numberOfChromosomesToSelect;
	
	public AbstractSelection(@Nonnegative int numberOfParentsToSelect) {
		this.numberOfChromosomesToSelect = numberOfParentsToSelect;
	}
	
	@Override
	public void setNumberOfChromosomesToSelect(@Nonnegative int numberOfParentsToSelect) {
		this.numberOfChromosomesToSelect = numberOfParentsToSelect;
	}

	@Override
	public int getNumberOfChromosomesToSelect() {
		return numberOfChromosomesToSelect;
	}
}
