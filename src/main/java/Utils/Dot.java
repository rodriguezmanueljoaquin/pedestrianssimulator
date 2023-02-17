package Utils;

public class Dot implements Zone {
    private final Vector position;

    public Dot(Vector position) {
        this.position = position;
    }


    @Override
    public Vector getMiddlePoint() {
        return position;
    }

    @Override
    public Vector getRandomPointInside() {
        return position;
    }

    @Override
    public boolean isPointInside(Vector point) {
        return position.distance(point) < Constants.DOUBLE_EPSILON;
    }
}
