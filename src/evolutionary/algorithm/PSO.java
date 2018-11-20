package evolutionary.algorithm;

import java.util.Arrays;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import evolutionary.inertion.IInertionProvider;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.solution.Particle;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.stopper.IOptimisationStopper;
import util.VectorUtils;
import utilities.random.RNGProvider;

public class PSO implements IOptimizationAlgorithm<Particle>{
		
	private final IStartPopulationGenerator<Particle> startPopulationGenerator;
	private final IOptimisationStopper<Particle> stopper;
	private final IInertionProvider inertionProvider;
	private final double individualFactor;
	private final double globalFactor;
	
	private ToDoubleFunction<double[]> function;

	private double xMin;
	private double xMax;
	private double vMin;
	private double vMax;

	private final int d;


	public PSO(IStartPopulationGenerator<Particle> startPopulationGenerator, 
			IOptimisationStopper<Particle> stopper,
			IInertionProvider inertionProvider,
			double individualFactor, double globalFactor,
			double xMin, double xMax, double vMin, double vMax,
			int d,
			ToDoubleFunction<double[]> function 
			) {
		
				this.startPopulationGenerator = startPopulationGenerator;
				this.stopper = stopper;
				this.inertionProvider = inertionProvider;
				this.individualFactor = individualFactor;
				this.globalFactor = globalFactor;
				this.xMin = xMin;
				this.xMax = xMax;
				this.vMin = vMin;
				this.vMax = vMax;
				this.d = d;
				this.function = function;
	}
	
	@Override
	public Particle run() {
		
		Random random = RNGProvider.getRandom();
		Particle[] population = startPopulationGenerator.generate();
		double[] populationFunctionValues = new double[population.length];
		
		boolean isFullGlobal = d>population.length/2 || d<0;
			
		double[] globallyBestFoundSolution = null;
		double globallyBestFoundSolutionQuality = Double.POSITIVE_INFINITY;
				
		int epochCount = 0;
		
		do{
			for(int i=0; i<population.length;i++){
				//particle evaluation
				Particle particle = population[i];
				populationFunctionValues[i] = function.applyAsDouble(particle.getPosition());
			}

			for(int i=0; i<population.length;i++){
				//local best solution update
				Particle particle = population[i];
				double particlePositionQuality = populationFunctionValues[i];
				if(particlePositionQuality<particle.getBestPositionQuality()){
					population[i].setBestPosition(particle.getPosition(), particlePositionQuality);
				}
			}

			boolean update = false;
			for(int i=0; i<population.length;i++){
				//globally best solution update
				Particle particle = population[i];
				double[] particlePosition = particle.getPosition();
				double particlePositionQuality = populationFunctionValues[i];
				if(particlePositionQuality<globallyBestFoundSolutionQuality || globallyBestFoundSolution==null){
					update = true;
					globallyBestFoundSolution = Arrays.copyOf(particlePosition, particlePosition.length);
					globallyBestFoundSolutionQuality = particlePositionQuality;
				}
			}			
			if(update){
				System.out.println("Iteration: " + epochCount + " Error: " + globallyBestFoundSolutionQuality);
			}

			double inertion = inertionProvider.provide(epochCount);
			for(int i=0; i<population.length;i++){
				//position update
				Particle particle = population[i];
				double[] particlePosition = particle.getPosition();
				double[] particleBestKnownPosition = particle.getBestKnownPosition();
				double[] particleSpeed = particle.getSpeed();
				
				double[] speedTimesInertion = VectorUtils.multiplyByScalar(particleSpeed, inertion, false);
				double[] individualComponent = 
						VectorUtils.multiplyByScalar(
								VectorUtils.subtract(particleBestKnownPosition, particlePosition,false),
								individualFactor*random.nextDouble(), false);
				double[] bestCollectivelyFound;
				if(isFullGlobal){
					bestCollectivelyFound = globallyBestFoundSolution;
				}
				else{
					double bestCollectivelyFoundQuality = particle.getBestPositionQuality();
					bestCollectivelyFound = particleBestKnownPosition;
					
					for(int j=1; j<=d;j++){
						Particle nextParticle = population[(i+j)%population.length];
						double quality = nextParticle.getBestPositionQuality();
						if(quality<bestCollectivelyFoundQuality){
							bestCollectivelyFoundQuality = quality;
							bestCollectivelyFound = nextParticle.getBestKnownPosition();
						}
						
						
						Particle previousParticle = population[(i-j+population.length)%population.length];
						quality = previousParticle.getBestPositionQuality();
						if(quality<bestCollectivelyFoundQuality){
							bestCollectivelyFoundQuality = quality;
							bestCollectivelyFound = previousParticle.getBestKnownPosition();
						}
						
					}
					
				}

				double[] socialComponent = VectorUtils.multiplyByScalar(
						VectorUtils.subtract(bestCollectivelyFound, particlePosition,false)
						, globalFactor*random.nextDouble(), false);
				
				for(int j=0; j<particleSpeed.length;j++){
					particleSpeed[j] = speedTimesInertion[j]+individualComponent[j]+socialComponent[j];
					particleSpeed[j] = Math.min(vMax, particleSpeed[j]);
					particleSpeed[j] = Math.max(vMin, particleSpeed[j]);
					
					particlePosition[j] +=particleSpeed[j];
					particlePosition[j] = Math.min(xMax, particlePosition[j]);
					particlePosition[j] = Math.max(xMin, particlePosition[j]);
				}
			}				
			
			epochCount++;
						
		}while(!stopper.shouldStop(population, populationFunctionValues, populationFunctionValues));
		
		@SuppressWarnings("null")
		Particle particle = new Particle(globallyBestFoundSolution.length);
		double[] particlePosition = particle.getPosition();
		
		for(int i=0; i<particlePosition.length;i++){
			particlePosition[i] = globallyBestFoundSolution[i];
		}
		
		return particle;
	}

}
