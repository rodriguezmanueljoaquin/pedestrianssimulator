package Environment.Objectives.Server;

import Agent.Agent;
import Environment.Objectives.ObjectiveType;
import Utils.Random.RandomGenerator;
import Utils.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class DynamicServer extends Server {
    private final Map<Integer, Double> attendingTimeMap = new HashMap<>();

    //Dynamic server is for an event that happens continuously.
    public DynamicServer(String id, int maxCapacity, Rectangle zone, RandomGenerator attendingDistribution, Queue queueLine) {
        super(id, maxCapacity, zone, attendingDistribution, queueLine);
    }

    private double getAttendingTime(Agent agent) {
        return this.attendingTimeMap.get(agent.getId());
    }

    @Override
    public void updateServer(double currentTime) {
        super.updateServer(currentTime);

        while (this.servingAgents.size() < this.maxAttendants && this.queueHandler.agentsInQueue() > 0) {
            this.startAttendingFirstAgentInQueue();
        }
    }

    private void startAttendingFirstAgentInQueue() {
        Agent agent = this.queueHandler.removeFromQueue();
        this.servingAgents.add(agent);
        this.serverPositionHandler.setNewPosition(agent.getId());
        this.attendingTimeMap.put(agent.getId(), this.attendingDistribution.getNewRandomNumber());
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return this.servingAgents.contains(agent);
    }

    @Override
    protected void freeAgent(Agent agent) {
        this.attendingTimeMap.remove(agent.getId());
        super.freeAgent(agent);
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        // returns true if one of the follow is suffixed:
        //      + Server is not serving to it
        //      + Agent is attending to server and has completed the required time
        return
                !this.attendingTimeMap.containsKey(agent.getId()) ||
                        (agent.getStartedAttendingAt() != null && currentTime - agent.getStartedAttendingAt() > getAttendingTime(agent));
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.DYNAMIC_SERVER;
    }
}
