package Utils;

public class Rectangle implements Zone {
    protected Vector x1, x2;
    /*
           ------- x2
        |           |
        x1 -------
     */

    public Rectangle(Vector x1, Vector x2) {
        // assign in such a way that this.x1 < this.x2 in both dimensions
        if(x1.getX() < x2.getX()) {
            this.x1 = new Vector(x1.getX(), 0.);
            this.x2 = new Vector(x2.getX(), 0.);
        } else {
            this.x1 = new Vector(x2.getX(), 0.);
            this.x2 = new Vector(x1.getX(), 0.);
        }

        if(x1.getY() < x2.getY()){
            this.x1.setY(x1.getY());
            this.x2.setY(x2.getY());
        } else {
            this.x1.setY(x2.getY());
            this.x2.setY(x1.getY());
        }

    }

    @Override
    public Vector getMiddlePoint() {
        return this.x1.add(this.x2).scalarMultiply(1 / 2.);
    }

    protected double getRandomDoubleInRange(Double min, Double max) {
        return Math.random() * (max - min) + min;
    }

    @Override
    public Vector getRandomPointInside() {
        return new Vector(this.getRandomDoubleInRange(x1.getX(), x2.getX()), this.getRandomDoubleInRange(x1.getY(), x2.getY()));
    }

    @Override
    public boolean isPointInside(Vector point) {
        return this.x1.getX() <= point.getX() &&
                point.getX() <= this.x2.getX() &&
                this.x1.getY() <= point.getY() &&
                point.getY() <= this.x2.getY();
    }
}