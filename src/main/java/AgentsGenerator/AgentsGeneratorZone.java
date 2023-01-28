package AgentsGenerator;

import Agent.AgentConstants;
import Utils.Rectangle;
import Utils.Vector;

public class AgentsGeneratorZone extends Rectangle {
    private int rowsQty, colsQty;

    public AgentsGeneratorZone(Vector x1, Vector x2) {
        super(x1, x2);
        this.rowsQty = (int) ((x2.getY() - x1.getY()) / (AgentConstants.MAX_RADIUS*3)); // *3 because we want the agent to have some free space on the cell
        this.colsQty = (int) ((x2.getX() - x1.getX()) / (AgentConstants.MAX_RADIUS*3));
    }

    public int getZoneMatrixSize() {
        return this.rowsQty * this.colsQty;
    }

    public Vector getPositionByIndex(int index) {
        double xIndex = index / this.colsQty;
        double yIndex = index % this.rowsQty;
        return new Vector(
                this.getRandomDoubleInRange(xIndex + AgentConstants.MAX_RADIUS, xIndex + 2*AgentConstants.MAX_RADIUS),
                this.getRandomDoubleInRange(yIndex + AgentConstants.MAX_RADIUS, yIndex + 2*AgentConstants.MAX_RADIUS)
        );
    }
}
