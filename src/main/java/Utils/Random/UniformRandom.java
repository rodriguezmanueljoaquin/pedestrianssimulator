package Utils.Random;

import java.util.Random;

public class UniformRandom implements RandomInterface{
    double from;
    double to;

    public UniformRandom(double from, double to){
        this.from = from;
        this.to = to;
    }

    @Override
    public Double getNewRandomNumber() {
        return Math.random()*(to - from) + from;
    }

    @Override
    public Double getMean(){
        return (to + from)/2;
    }


}
