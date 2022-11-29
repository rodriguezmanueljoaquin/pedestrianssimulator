package Walls;


import Utils.Vector;

public class HorizontalWall extends Wall {
    public HorizontalWall(Vector A, Vector B) {
        super(A, B);
    }

    @Override
    public Vector getClosestPoint(Vector x) {
        if (x.getX() <= this.getA().getX())
            return this.getA();
        if (x.getX() >= this.getB().getX())
            return this.getB();
        return new Vector(x.getX(),this.getB().getY());
    }
}
