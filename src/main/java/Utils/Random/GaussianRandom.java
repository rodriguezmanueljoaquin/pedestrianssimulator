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
        return this.random.nextGaussian() * std + mean;
    }

    @Override
    public double getMean() {
        return mean;
    }
}
