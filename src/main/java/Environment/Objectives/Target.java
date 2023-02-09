package Environment.Objectives;


import Agent.Agent;
import Utils.Vector;

public class Target implements Objective {
    private final Vector position;
    private final String groupId;
    private final Double attendingTime; //milliseconds needed to complete task
    private final Double attendingRadius; //radius at which the target can be attended

    public Target(String groupId, Vector position, Double attendingTime, Double attendingRadius) {
        this.position = position;
        this.attendingTime = attendingTime;
        this.groupId = groupId;
        this.attendingRadius = attendingRadius;
    }

    public Double getAttendingRadius(){
        return attendingRadius;
    }

    @Override
    public Vector getPosition(Agent agent) {
        return position;
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
        return position;
    }

    @Override
    public Boolean reachedObjective(Agent agent) {
        return agent.distance(getCentroidPosition()) < this.attendingRadius;
    }
}
