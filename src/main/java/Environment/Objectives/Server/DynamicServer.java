package Environment.Objectives.Server;

import Agent.Agent;
import Environment.Objectives.ObjectiveType;
import Utils.Rectangle;

public class DynamicServer extends Server {

    //Dynamic event is for an event that happens continuously.
    public DynamicServer(String id, int maxCapacity, Rectangle zone, double attendingTime, QueueLine queueLine) {
        super(id, maxCapacity, zone, attendingTime, queueLine);
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
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return this.servingAgents.contains(agent);
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        // returns true when agent has started attending the server and also has completed the time required of attending
        return agent.getStartedAttendingAt() != null && currentTime - agent.getStartedAttendingAt() > this.attendingTime;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.DYNAMIC_SERVER;
    }
}
