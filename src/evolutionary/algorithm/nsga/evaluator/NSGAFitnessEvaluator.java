package evolutionary.algorithm.nsga.evaluator;

import java.util.LinkedHashSet;
import java.util.List;

import util.VectorUtils;

public class NSGAFitnessEvaluator {
	
	public static enum Type{
		Decision,Objective
	}

	protected final double sigm;
	protected final double epsilon;
	protected final double alpha;
	private double[] min;
	private double[] max;
	private Type type;

	public NSGAFitnessEvaluator(double sigm, double epsilon,double alpha,double[] min, double[] max,Type type) {
		this.sigm = sigm;
		this.epsilon = epsilon;
		this.alpha = alpha;
		this.min = min;
		this.max = max;
		this.type = type;
	}
	
	public void evaluate(double[][] solutions, double[][] objectives, double[] fitness, List<LinkedHashSet<Integer>> fronts){
		
		double Fmin = solutions.length+epsilon;
		double[] density = new double[solutions.length];
		
		for(int i=0,size = fronts.size(); i<size;i++){
			LinkedHashSet<Integer> front = fronts.get(i);
			for(int k:front){
				for(int j:front){
					double distance = (type.equals(Type.Decision)) ? 
							VectorUtils.euclidDistance(solutions[k], solutions[j],min,max):
							VectorUtils.euclidDistance(objectives[k], objectives[j]);
					if(distance<sigm)
						density[k]+=1.0-Math.pow((distance/sigm),alpha);
						
				}
			}

			@SuppressWarnings("hiding")
			double min = Double.POSITIVE_INFINITY;
			for(int j:front){
				fitness[j] = (Fmin-epsilon)/density[j];
				min = Math.min(min, fitness[j]);
			}

			Fmin = min;
		}

	}
	
}
