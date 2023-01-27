package AgentsGenerator;

import Agent.AgentConstants;
import Utils.Rectangle;
import Utils.Vector;

public class AgentsGeneratorZone extends Rectangle {
    private int rowsQty, colsQty;

    public AgentsGeneratorZone(Vector x1, Vector x2) {
        super(x1, x2);
        this.rowsQty = (int) ((x2.getY() - x1.getY()) / AgentConstants.MAX_RADIUS);
        this.colsQty = (int) ((x2.getX() - x1.getX()) / AgentConstants.MAX_RADIUS);
    }

    public int getZoneMatrixSize() {
        return this.rowsQty * this.colsQty;
    }

    public Vector getPositionByIndex(int index) {
        int xIndex = index / this.colsQty;
        int yIndex = index % this.rowsQty;
        return new Vector(
                this.getRandomDoubleInRange(xIndex - AgentConstants.MAX_RADIUS / 2, xIndex + AgentConstants.MAX_RADIUS / 2),
                this.getRandomDoubleInRange(yIndex - AgentConstants.MAX_RADIUS / 2, yIndex + AgentConstants.MAX_RADIUS / 2)
        );
    }
}
