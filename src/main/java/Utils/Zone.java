package Utils;

public interface Zone {
    Vector getCentroid();

    Vector getRandomPointInside();

    boolean isPointInside(Vector point);
}
