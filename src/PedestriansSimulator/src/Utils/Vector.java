package Utils;

public class Vector {
    Double x;
    Double y;
    public Vector(Double x, Double y){
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Vector setX(Double x) {
        this.x = x;
        return this;
    }

    public Double getY() {
        return y;
    }

    public Vector setY(Double y) {
        this.y = y;
        return this;
    }

    public Vector multiply(Vector e){
        return this.setX(this.getX() * e.getX()).setY(this.getY() * e.getY());
    }

    public Vector scalarMultiply(Double e){
        return multiply(new Vector(e,e));
    }

    public Vector add(Vector e){
        return this.setX(this.getX() + e.getX()).setY(this.getY() + e.getY());
    }

    public Vector substract(Vector e){
        return this.setX(this.getX() - e.getX()).setY(this.getY() - e.getY());
    }

    public Double distance(Vector e){
        double distanceX = Math.abs(this.getX() - e.getX());
        double distanceY = Math.abs(this.getY() - e.getY());

        return Math.sqrt(Math.pow(distanceX,2) + Math.pow(distanceY,2));
    }

    public Vector clone(){
        return new Vector(this.getX(),this.getY());
    }
}
