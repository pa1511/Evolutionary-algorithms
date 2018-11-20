package evolutionary.selection;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class TournamentSelection<T> extends AbstractSelection<T>{
	
	private @Nonnegative int tournamentSize;
	private boolean isElimination;
	private final @Nonnull Function<T[], Function<double[],Function<T[],IntConsumer>>> tournamentSelection;
	
	/**
	 * Tournament size 3 <br>
	 * Selects 2 parents
	 */
	public TournamentSelection() {
		this(3,2);
	}

	/**
	 * Selects 2 parents
	 */
	public TournamentSelection(@Nonnegative int tournamentSize) {
		this(tournamentSize,2);
	}
	
	public TournamentSelection(@Nonnegative int tournamentSize, @Nonnegative int numberOfChromosomesToSelect) {
		super(numberOfChromosomesToSelect);
		this.tournamentSize = tournamentSize;
		
		isElimination = false;
		
		tournamentSelection = population -> populationFitness -> selectedChromosomesHolder -> i -> {
			selectedChromosomesHolder[i] = runTournament(population,populationFitness);
		};
	}
	//==========================================================================================================================
	
	@Override
	public void selectFrom(@Nonnull T[] population,@Nonnull double[] populationFitness,@Nonnull T[] selectedChromosomesHolder) {
		PStreams.forEachIndexIn(numberOfChromosomesToSelect, parallel,tournamentSelection.apply(population).apply(populationFitness).apply(selectedChromosomesHolder));
	}

	public @Nonnull T runTournament(@Nonnull T[] population,@Nonnull double[] populationFitness) {
		
		int[] selectedUnitIndexes = selectTournamentParticipants(population, tournamentSize);

		//perform tournament
		int maxFitnessIndex = runTournament(populationFitness, selectedUnitIndexes,isElimination);

		//winner
		return population[maxFitnessIndex];
	}
	
	public static<T> int[] selectTournamentParticipants(@Nonnull T[] population,@Nonnegative int tournamentSize) {		
		int[] selectedUnitIndexes = new int[tournamentSize];
		selectTournamentParticipants(population, selectedUnitIndexes);		
		return selectedUnitIndexes;
	}

	public static<T> void selectTournamentParticipants(T[] population, int[] selectedUnitIndexes) {
		Random random = RNGProvider.getRandom();
		boolean[] selected = new boolean[population.length];
		selectTournamentParticipants(population, selectedUnitIndexes, selected, random);
	}

	public static<T> void selectTournamentParticipants(T[] population, int[] selectedUnitIndexes,
			boolean[] selected,Random random) {
		//select tournament participants
		for(int i=0; i<selectedUnitIndexes.length;i++){
			int selectedIndex =  random.nextInt(population.length);
			
			if(selected[selectedIndex]){
				do{
					selectedIndex = (selectedIndex+1)%population.length;
				}
				while(selected[selectedIndex]);
			}
			selectedUnitIndexes[i] = selectedIndex;
			selected[selectedIndex] = true;
			
		}
	}

	public static int runTournament(@Nonnull double[] populationFitness,@Nonnull int[] participantIndexes, boolean isElimination) {
		int maxFitnessIndex = participantIndexes[0];
		for(int i=1; i<participantIndexes.length;i++){
			if((isElimination && populationFitness[participantIndexes[i]]<populationFitness[maxFitnessIndex])
				|| 
				(!isElimination && populationFitness[participantIndexes[i]]>populationFitness[maxFitnessIndex])){
				maxFitnessIndex = participantIndexes[i];
			}
		}
		return maxFitnessIndex;
	}
	
	//==========================================================================================================================
	//Attributes getters and setters
	
	public void setTournamentSize(int tournamentSize) {
		this.tournamentSize = tournamentSize;
	}
		
	public void setElimination(boolean isElimination) {
		this.isElimination = isElimination;
	}
	
}
