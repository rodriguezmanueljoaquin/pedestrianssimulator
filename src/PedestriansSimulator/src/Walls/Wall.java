package Walls;


import Utils.Vector;

public abstract class Wall {
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

    //https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
    public Vector getClosestPoint(Vector P) {
        Vector A = this.A;
        Vector B = this.B;
        Vector u;
        Vector v;


        return P;
    }
//    private double getClosestDistance(Double x, Double y) {
//        double hypot = Math.sqrt(Math.pow(this.B - this.A,2) + Math.pow(this.y2 - this.y1,2));
//        return Math.abs((this.B - this.A)*(this.y1-y) - (this.A - x)*(this.y2 - this.y1)) / hypot;
//    }
//
//    private double getClosestAngle(Double x, Double y){
//        if(x > th)
//    }
}
