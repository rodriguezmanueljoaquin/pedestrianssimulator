package Environment.Objectives.Target;


import Agent.Agent;
import Environment.Objectives.ObjectiveType;
import Utils.Constants;
import Utils.Vector;
import Utils.Zone;

// Dot target was not requested, so it could be erased.
public class DotTarget implements Target {
    private final Zone zone;
    private final String groupId;
    private final Double attendingTime; //milliseconds needed to complete task

    public DotTarget(String groupId, Zone zone, Double attendingTime) {
        this.zone = zone;
        this.attendingTime = attendingTime;
        this.groupId = groupId;
    }

    @Override
    public Vector getPosition(Agent agent) {
        return zone.getMiddlePoint();
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        //true if the agent has started to attend and completed it "task"
        return currentTime - agent.getStartedAttendingAt() >= this.attendingTime;
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return true;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.TARGET;
    }

    @Override
    public Vector getCentroidPosition() {
        return zone.getMiddlePoint();
    }

    @Override
    public Boolean reachedObjective(Agent agent) {
        return agent.distance(zone.getMiddlePoint()) < Constants.DOUBLE_EPSILON;
    }
}
