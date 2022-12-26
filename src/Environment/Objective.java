package Environment;

import Agent.Agent;
import Utils.Vector;

public interface Objective {
    Vector getPosition(Agent agent);

    Boolean canAttend(Agent agent);

    Boolean hasFinishedAttending(Agent agent, double currentTime);

    default Boolean isQueue() {
        return false;
    }
}
