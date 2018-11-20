package evolutionary.inertion;

public class LinearInertionProvider implements IInertionProvider{

	private double min;
	private double interval;
	private int maxT;

	public LinearInertionProvider(double min, double max, int maxT) {
		this.min = min;
		this.maxT = maxT;
		this.interval = max-min;
	}
	
	@Override
	public double provide(int t) {
		return (1.0-(double)t/(double)maxT)*interval+min;
	}

}
