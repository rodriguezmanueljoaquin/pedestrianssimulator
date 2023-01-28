package Environment.Objectives.Server;

import Agent.Agent;
import Environment.Objectives.ObjectiveType;
import Utils.Rectangle;
import Utils.Vector;

public class StaticServer extends Server {
    //Static event is for an event that has a fixed schedule
    public StaticServer(String id, int maxCapacity, Rectangle zone, double startTime, double attendingTime) {
        super(id, maxCapacity, zone, attendingTime, null);
        this.startTime = startTime;
    }

    @Override
    public Vector getPosition(Agent agent) {
        // position can depend on agent current location,
        // if agent is far from server, returned position is the centroid
        // if it is close (inside)
        //      it is added to "clients",
        //      and it will start to be attended by the server (it may be freed just after if static server has already finished)
        if (!this.servingAgents.contains(agent)) {
            if (this.serverPositionHandler.isAgentInside(agent) && this.servingAgents.size() < this.maxAttendants) {
                this.servingAgents.add(agent);
                this.serverPositionHandler.setNewPosition(agent.getId());
            } else
                return this.serverPositionHandler.getCentroid();
        }

        return super.getPosition(agent); //returns position designated to agent
    }

    @Override
    public Boolean canAttend(Agent agent) {
        //If event has finished, agent will be freed on next frame.
        return this.servingAgents.contains(agent);
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        // returns true when server has ended it activity or when a new agent arrives in a moment where server is at maxCapacity
        return currentTime - this.startTime > this.attendingTime ||
                (!this.servingAgents.contains(agent) && this.servingAgents.size() >= this.maxAttendants);
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.STATIC_SERVER;
    }
}
