package Environment.Server;

import Agent.Agent;
import Utils.Rectangle;
import Utils.Vector;

public class DynamicServer extends Server {

    //Dynamic event is for an event that happens continuously.
    public DynamicServer(int maxCapacity, Rectangle zone, double startTime, double attendingTime, Vector A, Vector B) {
        super(maxCapacity, zone, startTime, attendingTime, A, B);
    }

    @Override
    public Boolean canAttend(Agent agent) {
        // if event has finished, agent will be freed on next frame.
        return this.servingAgents.contains(agent);
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        if (servingAgents.contains(agent) && currentTime - agent.getStartedAttendingAt() > attendingTime) {
            serverPositionHandler.removeAgent(agent.getId());
            servingAgents.remove(agent);
            return true;
        }
        return false;
    }
}
