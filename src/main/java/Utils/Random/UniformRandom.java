package Utils.Random;

public class UniformRandom extends RandomGenerator {
    private final double from;
    private final double to;

    public UniformRandom(long seed, double from, double to) {
        super(seed);
        if (from > to) {
            this.from = to;
            this.to = from;
        } else {
            this.from = from;
            this.to = to;
        }
    }

    @Override
    public double getNewRandomNumber() {
        return Math.random() * (this.to - this.from) + this.from;
    }

    @Override
    public double getMean() {
        return (this.to + this.from) / 2;
    }

    @Override
    public double getHighestMostPossibleValue() {
        return this.to;
    }
}
