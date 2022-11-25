package Walls;

import java.util.Vector;

public class VerticalWall extends Wall{
    public VerticalWall(Vector<Double> x1, Vector<Double> x2) {
        super(x1, x2);
    }

    @Override
    public Vector<Double> getClosestPoint(Vector<Double> x) {
        if(x.get(1) >= this.getX2().get(1))
            return this.getX2();
        else if(x.get(1) <= this.getX1().get(1))
            return this.getX1();

        Vector<Double> closestPoint = new Vector<>(2);
        closestPoint.set(0, this.getX1().get(0));
        closestPoint.set(1, x.get(1));
        return closestPoint;
    }
}
