package Utils;

public interface Zone {
    Vector getMiddlePoint();

    Vector getRandomPointInside();

    boolean isPointInside(Vector point);
}
