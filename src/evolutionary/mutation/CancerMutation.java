package evolutionary.mutation;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import optimization.solution.BitVectorSolution;
import utilities.random.RNGProvider;
import utilities.streamAndParallelization.PStreams;

public class CancerMutation extends AbstractMutation<BitVectorSolution>{

	private final @Nonnull Function<BitVectorSolution[], IntConsumer> cancerMutation;
	private final @Nonnull Consumer<BitVectorSolution> perUnitCancerMutation;
	
	public CancerMutation() {
		perUnitCancerMutation = child ->{
			Random random = RNGProvider.getRandom();
			
			boolean[] genes = child.getActualEncoding();
			
			int setCount = 0;
			int start = random.nextInt(genes.length);
			int end = (start-1+genes.length)%genes.length;
			int passed = 0;
			
			while(start!=end){
				passed++;
				if(genes[start])
					setCount++;
				double setShare = ((double)setCount)/(passed);
				genes[start] = (setShare+0.5)/2>random.nextDouble();
				start = (start+1)%genes.length;
			}
			
			double setShare = ((double)setCount)/(passed);
			int changed = 0;
			for(int j=0; j<genes.length;j++){
				if(setShare>0.5 && !genes[j]){
					genes[j] = true;
				}
				else if(setShare<0.5 && genes[j]){
					genes[j] = false;
				}
				changed++;
				if(changed>=genes.length*0.15)
					break;
			}
		};
		cancerMutation = children -> i -> perUnitCancerMutation.accept(children[i]);;
	}
	
	@Override
	public void mutate(@Nonnull BitVectorSolution[] children) {
		PStreams.forEachIndexIn(children.length, parallel, cancerMutation.apply(children));
	}

	@Override
	public void mutate(BitVectorSolution child) {
		perUnitCancerMutation.accept(child);
	}
	
}
