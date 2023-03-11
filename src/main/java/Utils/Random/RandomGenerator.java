package Utils.Random;

import java.util.Random;

public abstract class RandomGenerator {
    protected Random random;

    public RandomGenerator(long seed) {
        this.random = new Random(seed);
    }

    public abstract double getNewRandomNumber();

    public abstract double getMean();
}
