package Environment.Objectives;

import Agent.Agent;
import Agent.AgentConstants;
import Utils.Vector;

public interface Objective {
    Vector getPosition(Agent agent);

    Boolean canAttend(Agent agent);

    Boolean hasFinishedAttending(Agent agent, double currentTime);

    ObjectiveType getType();

    Vector getCentroidPosition();

    default Boolean reachedObjective(Agent agent) {
        return agent.distance(this.getPosition(agent)) < AgentConstants.MINIMUM_DISTANCE_TO_TARGET;
    }
}
