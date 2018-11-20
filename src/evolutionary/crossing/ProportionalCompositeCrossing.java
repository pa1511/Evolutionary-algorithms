package evolutionary.crossing;

import java.util.Arrays;
import java.util.Random;

import utilities.random.RNGProvider;

public class ProportionalCompositeCrossing<T> implements ICrossing<T>{
	
	private double[] chances;
	private ICrossing<T>[] crossings;
	
	public ProportionalCompositeCrossing(double[] chances, ICrossing<T>[] crossings) {
		this.chances = chances;
		this.crossings = crossings;
	}
	
	@Override
	public void cross(T[] chromosomes, T[] newChromosomesHolder) {
		selectCrossing().cross(chromosomes, newChromosomesHolder);;
	}

	private ICrossing<T> selectCrossing() {
		
		Random random = RNGProvider.getRandom();
		
		double chance = random.nextDouble();
		double sum = 0.0;
		for(int i=0; i<chances.length;i++){
			sum+= chances[i];
			if(sum>=chance)
				return crossings[i];
		}
		
		return crossings[random.nextInt(crossings.length)];
	}

	@Override
	public T[] cross(T[] chromosomes) {
		return selectCrossing().cross(chromosomes);
	}

	@Override
	public int getNumberOfChromosomesProducedForPopulationSize(int populationSize) {
		long distinct = Arrays.stream(crossings).mapToInt(crossing->crossing.getNumberOfChromosomesProducedForPopulationSize(populationSize)).distinct().count();
		
		if(distinct!=1){
			throw new IllegalStateException("Corssings composite whose elements produce a different number of chromosomes");
		}
		
		return crossings[0].getNumberOfChromosomesProducedForPopulationSize(populationSize);
	}

	@Override
	public void setParallel(boolean parallel) {
		for(ICrossing<T> crossing:crossings)
			crossing.setParallel(parallel);
	}

	@Override
	public boolean isParallel() {
		boolean isParallel = crossings[0].isParallel();
		boolean areAllTheSame = Arrays.stream(crossings).allMatch(crossing->crossing.isParallel()==isParallel);
		
		if(areAllTheSame)
			return isParallel;
		
		throw new IllegalStateException("Corssings composite whose elements have different parallel settings");
	}

}
