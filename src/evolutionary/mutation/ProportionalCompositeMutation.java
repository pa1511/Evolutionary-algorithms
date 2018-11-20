package evolutionary.mutation;

import java.util.List;
import java.util.Random;

import utilities.random.RNGProvider;

public class ProportionalCompositeMutation<T> implements IMutation<T> {

	private double[] chances;
	private List<IMutation<T>> mutations;

	public ProportionalCompositeMutation(double[] chances, List<IMutation<T>> mutations) {
		this.chances = chances;
		this.mutations = mutations;
	}
	
	@Override
	public void mutate(T[] children) {
		selectMutation().mutate(children);
	}


	@Override
	public void mutate(T child) {
		selectMutation().mutate(child);
	}


	private IMutation<T> selectMutation() {
		Random random = RNGProvider.getRandom();
		double chance = random.nextDouble();
		double sum = 0.0;
		for (int i = 0; i < chances.length; i++) {
			sum += chances[i];
			if (sum >= chance)
				return mutations.get(i);
		}

		return mutations.get(random.nextInt(mutations.size()));
	}
	
	@Override
	public void setParallel(boolean parallel) {
		for(IMutation<T> mutation:mutations)
			mutation.setParallel(parallel);
	}

	@Override
	public boolean isParallel() {
		boolean allMatch = mutations.stream().allMatch(IMutation::isParallel);
		if(allMatch)
			return mutations.get(0).isParallel();
		
		throw new IllegalStateException("Mutations have different parallel settings");
	}

}
