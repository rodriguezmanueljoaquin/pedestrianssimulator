package Environment.Server;

import Agent.Agent;
import Utils.Line;
import Utils.Rectangle;
import Utils.Vector;

public class DynamicServer extends Server {

    //Dynamic event is for an event that happens continuously.
    public DynamicServer(int maxCapacity, Rectangle zone, double attendingTime, Line queueLine) {
        super(maxCapacity, zone, 0, attendingTime, queueLine);
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return this.servingAgents.contains(agent);
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        if (this.servingAgents.contains(agent) && currentTime - agent.getStartedAttendingAt() > this.attendingTime) {
            this.freeAgent(agent);
            return true;
        }
        return false;
    }
}
