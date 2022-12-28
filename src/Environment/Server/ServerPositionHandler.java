package Environment.Server;

import Agent.Agent;
import Utils.Rectangle;
import Utils.Vector;

import java.util.HashMap;
import java.util.Map;

class ServerPositionHandler {
    // Handles the positions designated to the agents attending the server
    private final Rectangle zone;
    private final Map<Integer, Vector> occupiedPositions;

    public ServerPositionHandler(Rectangle zone) {
        this.zone = zone;
        this.occupiedPositions = new HashMap<>();
    }

    public Vector getMiddlePoint() {
        return this.zone.getMiddlePoint();
    }

    public boolean isAgentInside(Agent agent) {
        return this.zone.isPointInside(agent.getPosition());
    }

    public Vector setNewPosition(int id) {
        Vector newPosition;
        do {
            newPosition = this.zone.getRandomPointInside();
        } while (this.occupiedPositions.containsValue(newPosition));

        this.occupiedPositions.put(id, newPosition);
        return newPosition;
    }

    public Vector getOccupiedPosition(int id) {
        return this.occupiedPositions.get(id);
    }

    public void removeAgent(int id) {
        this.occupiedPositions.remove(id);
    }
}