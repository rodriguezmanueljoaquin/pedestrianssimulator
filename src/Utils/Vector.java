package Utils;

import java.util.Objects;

import static Utils.Constants.DOUBLE_EPSILON;

public class Vector {
    Double x;
    Double y;

    public Vector(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Integer x, Integer y) {
        this.x = (double) x;
        this.y = (double) y;
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

    public Vector multiply(Vector e) {
        return new Vector(this.getX() * e.getX(), this.getY() * e.getY());
    }

    public Double dotMultiply(Vector e) {
        return this.getX() * e.getX() + this.getY() * e.getY();
    }

    public Vector scalarMultiply(Double e) {
        return multiply(new Vector(e, e));
    }

    public Vector add(Vector e) {
        return new Vector(this.getX() + e.getX(), this.getY() + e.getY());
    }

    public Vector substract(Vector e) {
        return new Vector(this.getX() - e.getX(), this.getY() - e.getY());
    }

    public Vector normalize() {
        double hypot = Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));

        return new Vector(this.getX() / hypot, this.getY() / hypot);
    }

    public Double distance(Vector e) {
        double distanceX = Math.abs(this.getX() - e.getX());
        double distanceY = Math.abs(this.getY() - e.getY());

        return Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }

    public Vector clone() {
        return new Vector(this.getX(), this.getY());
    }

    @Override
    public String toString() {
        return this.getX() + ";" + this.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return Math.abs(this.x - vector.x) <= DOUBLE_EPSILON && Math.abs(this.y - vector.y) <= DOUBLE_EPSILON;
    }

    @Override
    public int hashCode() {
        // round to avoid epsilon error
        return Objects.hash(Math.round(this.x * DOUBLE_EPSILON) / DOUBLE_EPSILON, Math.round(this.x * DOUBLE_EPSILON) / DOUBLE_EPSILON);
    }
}
