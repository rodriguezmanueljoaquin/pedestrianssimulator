package AgentsGenerator;

import Agent.AgentConstants;
import Utils.Rectangle;
import Utils.Vector;

public class AgentsGeneratorZone extends Rectangle {
    private int rowsQty, colsQty;
    private final double CELL_SIZE = (AgentConstants.MAX_RADIUS_OF_ALL_AGENTS * 2) *2; // at least 2 agents fit in one cell
    public AgentsGeneratorZone(Vector x1, Vector x2) {
        super(x1, x2);
        this.rowsQty =  (int) Math.floor((this.x2.getY() - this.x1.getY()) / CELL_SIZE);
        this.colsQty = (int) Math.floor((this.x2.getX() - this.x1.getX()) / CELL_SIZE);
        if (this.rowsQty <= 0)
            this.rowsQty = 1;
        if (this.colsQty <= 0)
            this.colsQty = 1;
    }

    public int getZoneMatrixSize() {
        return this.rowsQty * this.colsQty;
    }

    public Vector getPositionByIndex(int index) {
        if(index > this.getZoneMatrixSize())
            throw new IllegalArgumentException("Index: " + index + " out of bounds: " + this.getZoneMatrixSize() + " in agents generator zone");

        double row = Math.floor((double) index / this.colsQty);
        double col = index % this.colsQty;
        Vector difference = this.x2.substract(this.x1).multiply(new Vector(1. / (this.rowsQty), 1. / (this.colsQty)));
        Vector pos = this.x1.add(difference.multiply(new Vector(row, col)));
        return pos.add(new Vector(
                this.getRandomDoubleInRange(CELL_SIZE/4, CELL_SIZE/2),
                this.getRandomDoubleInRange(CELL_SIZE/4, CELL_SIZE/2)
        ));
    }

    public int getIndexByPosition(Vector position) {
        Vector indexes = position.substract(this.x1).scalarMultiply(1/CELL_SIZE);
        return (int) (Math.floor(indexes.getX()) * this.colsQty + Math.floor(indexes.getY()));
    }
}
