package Environment;

import Agent.Agent;
import Utils.Vector;

public interface Objective {
    Vector getPosition(Agent agent);

    Boolean needsAttending(Agent agent);

    Boolean hasFinishedAttending(Agent agent, double currentTime);

    default Boolean isServer() {
        return false;
    }
}
