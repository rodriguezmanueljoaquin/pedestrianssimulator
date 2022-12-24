package Environment;

import Utils.Vector;
import Agent.Agent;

public interface Objective {
    Vector getPosition(Agent agent);
    Boolean hasToAttend(Agent agent);
    Boolean hasFinishedAttending(Agent agent, double startedAttendingTime, double currentTime);
    default Boolean isServer() {
        return false;
    }
}
