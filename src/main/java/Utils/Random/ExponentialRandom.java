package Utils.Random;

public class ExponentialRandom implements RandomInterface{
    double lambda;

    public ExponentialRandom(double lambda){
        this.lambda = lambda;
    }

    @Override
    public Double getNewRandomNumber() {
        //As Math.random() returns a number between [0, 1)
        //Notice the parenthesis
        //I have to do 1 - Math.random() so as to not get a log(0)
       return Math.log(1 - Math.random())/(-lambda);
    }

    @Override
    public Double getMean(){
        return 1/lambda;
    }
}
