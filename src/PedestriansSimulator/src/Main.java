import Utils.Vector;
import Walls.Wall;

public class Main {
    public static void main(String[] args) {
        Wall exampleWall = new Wall(new Vector(0,0),new Vector(0,2));
        Vector exampleVector = new Vector(1,1);
        System.out.println(exampleWall.getClosestPoint(exampleVector));

    }
}