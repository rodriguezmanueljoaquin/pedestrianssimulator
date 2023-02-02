package Environment.Objectives;


import Agent.Agent;
import Utils.Vector;

public class Target implements Objective {
    private final Vector position;
    private final String id;
    private final Double attendingTime; //milliseconds needed to complete task

    public Target(String id, Vector position, Double attendingTime) {
        this.position = position;
        this.attendingTime = attendingTime;
        this.id = id;
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
}
