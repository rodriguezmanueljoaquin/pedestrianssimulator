package AgentsGenerator;

import Agent.AgentConstants;
import Utils.Rectangle;
import Utils.Vector;

public class AgentsGeneratorZone extends Rectangle {
    private int rowsQty, colsQty;

    public AgentsGeneratorZone(Vector x1, Vector x2) {
        super(x1, x2);
        this.rowsQty = (int) ((x2.getY() - x1.getY()) / (AgentConstants.MAX_RADIUS_OF_ALL_AGENTS * 3)); // *3 because we want the agent to have some free space on the cell
        this.colsQty = (int) ((x2.getX() - x1.getX()) / (AgentConstants.MAX_RADIUS_OF_ALL_AGENTS * 3));
        if (this.rowsQty == 0)
            this.rowsQty = 1;
        if (this.colsQty == 0)
            this.colsQty = 1;
    }

    public int getZoneMatrixSize() {
        return this.rowsQty * this.colsQty;
    }

    public Vector getPositionByIndex(int index) {
        double row = Math.floor((double) index / this.colsQty);
        double col = index % this.colsQty;
        Vector difference = this.x2.substract(this.x1).multiply(new Vector(1. / (this.rowsQty), 1. / (this.colsQty)));
        Vector pos = this.x1.add(difference.multiply(new Vector(row, col)));
        return pos.add(new Vector(
                this.getRandomDoubleInRange(AgentConstants.MAX_RADIUS_OF_ALL_AGENTS, 2 * AgentConstants.MAX_RADIUS_OF_ALL_AGENTS),
                this.getRandomDoubleInRange(AgentConstants.MAX_RADIUS_OF_ALL_AGENTS, 2 * AgentConstants.MAX_RADIUS_OF_ALL_AGENTS)
        ));
    }
}
