package Utils;

public class Circle implements Zone{
    Vector middlePoint;
    Double radius;

    public Circle(Vector middlePoint, Double radius){
        this.middlePoint = middlePoint;
        this.radius = radius;
    }
    @Override
    public Vector getMiddlePoint() {
        return middlePoint;
    }

    @Override
    public Vector getRandomPointInside() {
        double r = radius * Math.sqrt(Math.random());
        double theta = Math.random() * 2 * Math.PI;
        Double xPos = middlePoint.getX() + r * Math.cos(theta);
        Double yPos = middlePoint.getY() + r * Math.sin(theta);
        return new Vector(xPos, yPos);
    }

    @Override
    public boolean isPointInside(Vector point) {
        return this.getMiddlePoint().distance(point) < radius;
    }
}
