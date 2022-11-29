package Walls;


import Utils.Vector;

public class VerticalWall extends Wall{
    public VerticalWall(Vector A, Vector B) {
        super(A, B);
    }

    @Override
    public Vector getClosestPoint(Vector x) {
        if(x.getY() >= this.getB().getY())
            return this.getB();
        else if(x.getY() <= this.getA().getY())
            return this.getA();

        return new Vector(this.getA().getX(), x.getY());
    }
}
