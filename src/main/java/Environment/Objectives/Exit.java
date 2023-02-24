package Environment.Objectives;

import Agent.Agent;
import Agent.AgentConstants;
import Environment.Wall;
import Utils.Constants;
import Utils.Vector;

public class Exit implements Objective {
    private final Wall exit;

    public Exit(Wall exit) {
        this.exit = exit;
    }

    public Wall getExitWall() {
        return exit;
    }

    @Override
    public Boolean reachedObjective(Agent agent) {
        return agent.distance(exit.getClosestPoint(agent.getPosition())) < AgentConstants.MINIMUM_DISTANCE_TO_TARGET;
    }

    @Override
    public Vector getPosition(Agent agent) {//Devuelvo el punto del medio, esto para tratr que extienda de objective
        return this.exit.getA().add(this.exit.getB()).scalarMultiply(0.5);
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return true;
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        return currentTime - agent.getStartedAttendingAt() > Constants.LEAVING_TIME;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.EXIT;
    }

    @Override
    public Vector getCentroidPosition() {
        return exit.getCentroid();
    }
}
