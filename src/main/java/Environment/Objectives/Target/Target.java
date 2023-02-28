package Environment.Objectives.Target;

import Agent.Agent;
import Environment.Objectives.Objective;
import Environment.Objectives.ObjectiveType;
import Utils.Random.RandomInterface;

import java.util.HashMap;
import java.util.Map;

public abstract class Target implements Objective {
    private final RandomInterface attendingDistribution;
    private final String groupId;
    private final Map<Integer, Double> attendingTimeMap = new HashMap<>();

    public Target(RandomInterface attendingDistribution, String groupId) {
        this.attendingDistribution = attendingDistribution;
        this.groupId = groupId;
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        //true if the agent has started to attend and completed it "task"
        return currentTime - agent.getStartedAttendingAt() >= this.getAttendingTime();
    }

    public Double getAttendingTime() {
        return this.attendingDistribution.getNewRandomNumber();
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return true;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.TARGET;
    }
}
