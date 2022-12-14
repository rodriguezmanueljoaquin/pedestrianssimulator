package Utils;

public class Rectangle {
    private Vector x1, x2;
    /*
           ------- x2
        |           |
        x1 -------
     */

    public Rectangle(Vector x1, Vector x2) {
        // check dots are as expected
        if (x1.getX() >= x2.getX() || x1.getY() >= x2.getY())
            throw new IllegalArgumentException("Rectangle arguments are not as expected.");

        this.x1 = x1;
        this.x2 = x2;
    }

    protected double getRandomDoubleInRange(Double min, Double max) {
        return Math.random() * (max - min) + min;
    }

    public Vector getRandomPointInside() {
        return new Vector(getRandomDoubleInRange(x1.getX(), x2.getX()), getRandomDoubleInRange(x1.getY(), x2.getY()));
    }
}
