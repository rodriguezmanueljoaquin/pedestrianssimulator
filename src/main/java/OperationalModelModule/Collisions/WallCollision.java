package OperationalModelModule.Collisions;

import Agent.Agent;
import Utils.Vector;

public class WallCollision {
    private final Agent agent;
    private final Vector wallClosestPoint;

    public WallCollision(Agent agent, Vector wallClosestPoint) {
        this.agent = agent;
        this.wallClosestPoint = wallClosestPoint;
    }

    public Agent getAgent() {
        return agent;
    }

    public Vector getWallClosestPoint() {
        return wallClosestPoint;
    }
}
