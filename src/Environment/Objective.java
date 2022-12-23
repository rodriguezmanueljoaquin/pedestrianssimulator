package Environment;

import Utils.Vector;

public interface Objective {
    Vector getPosition();
    Boolean hasToAttend();
    Boolean hasFinishedAttending(int agentId, double startedAttendingTime, double currentTime);
    default Boolean isServer() {
        return false;
    }
}
