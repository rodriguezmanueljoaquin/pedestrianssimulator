package Environment.Objectives.Target;


import Agent.Agent;
import Environment.Objectives.ObjectiveType;
import Utils.Random.RandomInterface;
import Utils.Vector;
import Utils.Zone;

public class BorderTarget implements Target {
    private final Zone zone;
    private final String groupId;
    private final RandomInterface attendingDistribution;


    public BorderTarget(String groupId, Zone zone, RandomInterface attendingDistribution) {
        this.zone = zone;
        this.attendingDistribution = attendingDistribution;
        this.groupId = groupId;
    }

    public Double getAttendingTime() {
        return attendingDistribution.getNewRandomNumber();
    }

    @Override
    public Vector getPosition(Agent agent) {
        return zone.getMiddlePoint();
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        //true if the agent has started to attend and completed it "task"
        return currentTime - agent.getStartedAttendingAt() >= getAttendingTime();
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
        return zone.isPointInside(agent.getPosition());
    }
}
