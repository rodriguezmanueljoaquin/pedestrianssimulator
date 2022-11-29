package Walls;

import Utils.Vector;

public class Wall {
    private final Vector A, B; // x1 < x2

    public Wall(Vector A, Vector B) {
        this.A = A;
        this.B = B;
        if (A.getX() > B.getX()) {
            this.A.setX(B.getX());
            this.B.setX(A.getX());
        }

        if (A.getY() > B.getY()) {
            this.A.setY(B.getY());
            this.B.setY(A.getY());
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
        return String.format("%f;%f;%f;%f", this.A.getX(), this.A.getY(), this.B.getX(), this.B.getY());
    }

    //https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
    public Vector getClosestPoint(Vector P) {
        Vector u = A.substract(P);
        Vector v = B.substract(A);

        double t = - v.dotMultiply(u) / v.dotMultiply(v);

        if(t >= 0 && t <= 1){
            return A.scalarMultiply(1 - t).add(B.scalarMultiply(t));
        }
        if(P.distance(A) > P.distance(B))
            return B;
        return A;
    }

    private double getClosestDistance(Vector P){
        return P.distance(this.getClosestPoint(P));
    }
}
