package Utils.Random;

public class ExponentialRandom extends RandomGenerator {
    private final double lambda;

    public ExponentialRandom(long seed, double mean) {
        super(seed);
        this.lambda = 1 / mean;
    }

    @Override
    public double getNewRandomNumber() {
        return Math.log(1 - this.random.nextDouble()) / (-this.lambda);
    }

    @Override
    public double getMean() {
        return 1 / this.lambda;
    }

    @Override
    public double getHighestMostPossibleValue() {
        return Math.log(0.01) / (-this.lambda);
    }
}
