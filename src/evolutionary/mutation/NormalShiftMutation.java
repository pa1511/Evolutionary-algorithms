package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.utility.OptimizationAlgorithmsUtility;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;
import optimization.solution.DoubleArraySolution;

public class NormalShiftMutation extends AbstractMutation<DoubleArraySolution>{

	private final @Nonnull double[] min;
	private final @Nonnull double[] max;
	private final @Nonnull Function<DoubleArraySolution[], IntConsumer> normalShiftMutation;
	private final @Nonnull Consumer<DoubleArraySolution> normalShiftMutationPerUnit;
	
	public NormalShiftMutation(double mutationChance, double value, double sigma, @Nonnull double[] min, @Nonnull double[]  max, boolean limitValue) {
		super(mutationChance);
		this.min = min;
		this.max = max;
		normalShiftMutationPerUnit = unit -> {
			Random random = RNGProvider.getRandom();
			double[] values = unit.values;
			for(int j=0; j<values.length;j++){
				if(random.nextDouble()<=mutationChance){
					double childValue = values[j]+random.nextGaussian()*sigma+value;
					if(limitValue){
						double minValue = (j<min.length) ? min[j] : min[0];
						double maxValue = (j<max.length) ? max[j] : max[0];
						childValue = OptimizationAlgorithmsUtility.placeValueInInterval(minValue, maxValue, childValue);
					}
					
					values[j]=childValue;
				}
			}
		};
		normalShiftMutation = children -> i -> normalShiftMutationPerUnit.accept(children[i]);
	}

	public NormalShiftMutation(double mutationChance, double sigma, double minAllowedValue, double  maxAllowedValue) {
		this(mutationChance,0,sigma,minAllowedValue,maxAllowedValue);
	}

	
	public NormalShiftMutation(double mutationChance, double sigma, double[] min, double[]  max) {
		this(mutationChance,0,sigma,min,max);
	}	

	public NormalShiftMutation(double mutationChance, double value, double sigma, double minAllowedValue, double  maxAllowedValue) {
		this(mutationChance,value,sigma,new double[1],new double[1]);
		this.max[0] = maxAllowedValue;
		this.min[0] = minAllowedValue;
	}
	
	public NormalShiftMutation(double mutationChance, double value, double sigma, @Nonnull double[] min, @Nonnull double[]  max) {
		this(mutationChance,value,sigma,min,max,true);
	}

	public NormalShiftMutation(double mutationChance, double value, double sigma) {
		this(mutationChance,value,sigma,new double[]{},new double[]{}, false);
	}
	
	public NormalShiftMutation(double mutationChance, double sigma) {
		this(mutationChance,0,sigma);
	}
	
	@Override
	public void mutate(@Nonnull DoubleArraySolution[] children) {
		PStreams.forEachIndexIn(children.length, parallel, normalShiftMutation.apply(children));
	}

	@Override
	public void mutate(DoubleArraySolution child) {
		normalShiftMutationPerUnit.accept(child);
	}
}
