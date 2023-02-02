package Environment;

import Utils.Vector;

import java.awt.geom.Line2D;
import java.util.Locale;

public class Wall {
    private final Vector A, B; // where A.x < B.x, if A.x == B.x, A.y < B.y

    public Wall(Vector x1, Vector x2) {
        if (x1.getX() > x2.getX()) {
            this.A = x1.clone();
            this.B = x2.clone();
        } else if (x1.getX() < x2.getX()) {
            this.A = x2.clone();
            this.B = x1.clone();
        } else {
            // case where x1.getX() == x2.getX()
            if (x1.getY() > x2.getY()) {
                this.A = x2.clone();
                this.B = x1.clone();
            } else {
                this.A = x1.clone();
                this.B = x2.clone();
            }
        }
    }

    public Vector getA() {
        return this.A;
    }

    public Vector getB() {
        return this.B;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%f;%f;%f;%f", this.A.getX(), this.A.getY(), this.B.getX(), this.B.getY());
    }

    // For CPM.CPM usages
    // method from: https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
    public Vector getClosestPoint(Vector P) {
        Vector u = A.substract(P);
        Vector v = B.substract(A);

        double t = -v.dotMultiply(u) / v.dotMultiply(v);

        if (t >= 0 && t <= 1) {
            return A.scalarMultiply(1 - t).add(B.scalarMultiply(t));
        }
        if (P.distance(A) > P.distance(B))
            return B;
        return A;
    }

    public boolean intersectsLine(Vector origin, Vector destiny) {
        return Line2D.Double.linesIntersect(
                origin.getX(), origin.getY(), destiny.getX(), destiny.getY(),
                this.A.getX(), this.A.getY(), this.B.getX(), this.B.getY()
        );
    }

    public boolean contains(Vector position) {
        return 0 == Line2D.Double.ptLineDist(
                this.A.getX(), this.A.getY(), this.B.getX(), this.B.getY(),
                position.getX(), position.getY()
        );
    }

    public Vector getCentroid() {
        return this.A.add(this.B.substract(this.A).scalarMultiply(0.5));
    }
}
