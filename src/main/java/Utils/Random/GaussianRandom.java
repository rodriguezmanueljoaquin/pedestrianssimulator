package Utils.Random;

import java.util.Random;

public class GaussianRandom implements RandomInterface {
    double mean;
    double std;
    Random random;

    public GaussianRandom(double mean, double std) {
        this.mean = mean;
        this.std = std;
        this.random = new Random();
    }

    @Override
    public Double getNewRandomNumber() {
        return this.random.nextGaussian() * std + mean;
    }


    @Override
    public Double getMean() {
        return mean;
    }
}
