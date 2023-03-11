package Utils.Random;

public class ExponentialRandom extends RandomGenerator {
    private final double alpha;

    public ExponentialRandom(long seed, double alpha) {
        super(seed);
        this.alpha = alpha;
    }

    //ALPHA IS STD, it is usually defined with lambda = 1/alpha;
    //However, it is more intuitive for the end user to define the STD
    @Override
    public double getNewRandomNumber() {
        //As Math.random() returns a number between [0, 1)
        //Notice the parenthesis
        //I have to do 1 - Math.random() so as to not get a log(0)
        return Math.log(1 - this.random.nextDouble()) * (-alpha);
    }

    @Override
    public double getMean() {
        return alpha;
    }
}
