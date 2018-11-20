package evolutionary.mutation;

import utilities.streamAndParallelization.AbstractParallelizable;

public abstract class AbstractMutation<T> extends AbstractParallelizable implements IMutation<T>{

	protected double mutationChance = 0.0;
	
	public AbstractMutation() {
		this(1.0);
	}
	
	public AbstractMutation(double mutationChance) {
		this.mutationChance = mutationChance;
	}
		
}
