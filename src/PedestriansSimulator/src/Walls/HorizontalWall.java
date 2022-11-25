package Walls;

import java.util.Vector;

public class HorizontalWall extends Wall {
    public HorizontalWall(Vector<Double> x1, Vector<Double> x2) {
        super(x1, x2);
    }

    @Override
    public Vector<Double> getClosestPoint(Vector<Double> x) {
        if (x.get(0) <= this.getX1().get(0))
            return this.getX1();
        if (x.get(0) >= this.getX2().get(0))
            return this.getX2();

        Vector<Double> closestPoint = new Vector<>(2);
        closestPoint.set(0, x.get(0));
        closestPoint.set(1, this.getX1().get(1));
        return closestPoint;
    }
}
