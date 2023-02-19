package Utils;

public class Rectangle implements Zone {
    protected Vector x1, x2;
    /*
           ------- x2
        |           |
        x1 -------
     */

    public Rectangle(Vector x1, Vector x2) {
        if (x1.getX() > x2.getX() || (x1.getX() == x2.getX() && x1.getY() > x2.getY())) {
            // arguments are inverted
            this.x1 = x2;
            this.x2 = x1;
        } else {
            this.x1 = x1;
            this.x2 = x2;
        }
    }

    @Override
    public Vector getMiddlePoint() {
        return x1.add(x2).scalarMultiply(1 / 2.);
    }

    protected double getRandomDoubleInRange(Double min, Double max) {
        return Math.random() * (max - min) + min;
    }

    @Override
    public Vector getRandomPointInside() {
        return new Vector(getRandomDoubleInRange(x1.getX(), x2.getX()), getRandomDoubleInRange(x1.getY(), x2.getY()));
    }

    @Override
    public boolean isPointInside(Vector point) {
        return this.x1.getX() <= point.getX() && point.getX() <= this.x2.getX() && this.x1.getY() <= point.getY() && point.getY() <= this.x2.getY();
    }

}