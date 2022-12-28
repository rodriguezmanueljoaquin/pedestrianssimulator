package Environment;


import Agent.Agent;
import Utils.Vector;

public class Target implements Objective {
    private final Vector position;
    private final Integer id;
    private final Double attendingTime; //milliseconds needed to complete task

    public Target(Integer id, Vector position, Double attendingTime) {
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
        return agent.getStartedAttendingAt() != null && currentTime - agent.getStartedAttendingAt() >= this.attendingTime;
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return true;
    }

}
