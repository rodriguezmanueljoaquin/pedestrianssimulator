package Utils;

public class Line {
    private final Vector x1, x2;
    // x1 is the start

    public Line(Vector x1, Vector x2) {
        this.x1 = x1;
        this.x2 = x2;
    }

    public Line(Line line) {
        this.x1 = line.getX1();
        this.x2 = line.getX2();
    }

    public Vector getX1() {
        return x1;
    }

    public Vector getX2() {
        return x2;
    }
}
