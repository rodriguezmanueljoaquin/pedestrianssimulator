package Utils.Random;

public class GaussianRandom extends RandomGenerator {
    private double mean;
    private double std;

    public GaussianRandom(long seed, double mean, double std) {
        super(seed);
        this.mean = mean;
        this.std = std;
    }

    @Override
    public double getNewRandomNumber() {
        return this.random.nextGaussian() * this.std + this.mean;
    }

    @Override
    public double getMean() {
        return this.mean;
    }

    @Override
    public double getHighestMostPossibleValue() {
        return this.mean + 3 * this.std; //highest possible value 99.7% of the time
    }
}
