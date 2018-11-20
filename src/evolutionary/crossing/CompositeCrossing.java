package evolutionary.crossing;

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import utilities.random.RNGProvider;

public class CompositeCrossing<T> implements ICrossing<T>{

	private final @Nonnull List<ICrossing<T>> crossings;

	public CompositeCrossing(List<ICrossing<T>> crossings) {
		this.crossings = crossings;
	}
	
	@Override
	public @Nonnull T[] cross(@Nonnull T[] parents) {
		return crossings.get(RNGProvider.getRandom().nextInt(crossings.size())).cross(parents);
	}

	@Override
	public void cross(@Nonnull T[] chromosomes,@Nonnull T[] newChromosomesHolder) {
		crossings.get(RNGProvider.getRandom().nextInt(crossings.size())).cross(chromosomes,newChromosomesHolder);
	}

	
	@Override
	public @Nonnegative int getNumberOfChromosomesProducedForPopulationSize(@Nonnegative int populationSize) {
		long distinct = crossings.stream().mapToInt(crossing->crossing.getNumberOfChromosomesProducedForPopulationSize(populationSize)).distinct().count();
		
		if(distinct!=1){
			throw new IllegalStateException("Corssings composite whose elements produce a different number of chromosomes");
		}
		
		return crossings.get(0).getNumberOfChromosomesProducedForPopulationSize(populationSize);
	}

	@Override
	public void setParallel(boolean isParallel) {
		crossings.forEach(crossing->crossing.isParallel());
	}

	@Override
	public boolean isParallel() {
		boolean isParallel = crossings.get(0).isParallel();
		boolean areAllTheSame = crossings.stream().allMatch(crossing->crossing.isParallel()==isParallel);
		
		if(areAllTheSame)
			return isParallel;
		
		throw new IllegalStateException("Corssings composite whose elements have different parallel settings");
	}

}
