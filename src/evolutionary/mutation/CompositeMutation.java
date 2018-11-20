package evolutionary.mutation;

import java.util.List;

import javax.annotation.Nonnull;

import utilities.random.RNGProvider;

public class CompositeMutation<T> implements IMutation<T>{

	private final @Nonnull List<IMutation<T>> mutations;

	public CompositeMutation(@Nonnull List<IMutation<T>> mutations) {
		this.mutations = mutations;
	}

	@Override
	public void mutate(@Nonnull T[] children) {
		mutations.get(RNGProvider.getRandom().nextInt(mutations.size())).mutate(children);
	}
	
	@Override
	public void mutate(@Nonnull T child) {
		mutations.get(RNGProvider.getRandom().nextInt(mutations.size())).mutate(child);
	}

	@Override
	public boolean isParallel() {
		boolean allMatch = mutations.stream().allMatch(IMutation::isParallel);
		if(allMatch)
			return mutations.get(0).isParallel();
		
		throw new IllegalStateException("Mutations have different parallel settings");
	}

	@Override
	public void setParallel(boolean parallel) {
		mutations.forEach(mutation->mutation.setParallel(parallel));
	}

}
