package evolutionary.mutation;

import javax.annotation.Nonnull;

import utilities.streamAndParallelization.IParallelizable;

public interface IMutation<T> extends IParallelizable {

	public void mutate(@Nonnull T[] children);

	public void mutate(T child);

}
