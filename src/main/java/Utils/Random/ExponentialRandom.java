package Utils.Random;

public class ExponentialRandom implements RandomInterface {
    double alpha;

    public ExponentialRandom(double alpha) {
        this.alpha = alpha;
    }

    //ALPHA IS STD, it is usually defined with lambda = 1/alpha;
    //However, it is more intuitive for the end user to define the STD
    @Override
    public Double getNewRandomNumber() {
        //As Math.random() returns a number between [0, 1)
        //Notice the parenthesis
        //I have to do 1 - Math.random() so as to not get a log(0)
        return Math.log(1 - Math.random()) * (-alpha);
    }

    @Override
    public Double getMean() {
        return alpha;
    }
}
