package Walls;

import java.util.Vector;

public abstract class Wall {
    private final Vector<Double> x1, x2; // x1 < x2

    public Wall(Vector<Double> x1, Vector<Double> x2) {
        this.x1 = x1;
        this.x2 = x2;
        if (x1.get(0) > x2.get(0)) {
            this.x1.set(0, x2.get(0));
            this.x2.set(0, x1.get(0));
        }

        if (x1.get(1) > x2.get(1)) {
            this.x1.set(1, x2.get(1));
            this.x2.set(1, x1.get(1));
        }
    }

    public Vector<Double> getX1() {
        return this.x1;
    }

    public Vector<Double> getX2() {
        return this.x2;
    }

    public abstract Vector<Double> getClosestPoint(Vector<Double> x);

//    private double getClosestDistance(Double x, Double y) {
//        double hypot = Math.sqrt(Math.pow(this.x2 - this.x1,2) + Math.pow(this.y2 - this.y1,2));
//        return Math.abs((this.x2 - this.x1)*(this.y1-y) - (this.x1- x)*(this.y2 - this.y1)) / hypot;
//    }
//
//    private double getClosestAngle(Double x, Double y){
//        if(x > th)
//    }
}
