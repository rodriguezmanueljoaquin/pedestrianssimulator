package Environment.Server;

import Agent.Agent;
import Utils.Line;
import Utils.Rectangle;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class StaticServer extends Server {
    //Static event is for an event that has a fixed schedule
    public StaticServer(int maxCapacity, Rectangle zone, double startTime, double attendingTime) {
        super(maxCapacity, zone, startTime, attendingTime, null);
        this.startTime = startTime;
    }

    @Override
    public Vector getPosition(Agent agent) {
        // position can depend on agent current location, as it may vary only inside server zone and when the agent is inside
        if(!this.servingAgents.contains(agent)) {
            return this.serverPositionHandler.getMiddlePoint();
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
        if(currentTime - this.startTime > this.attendingTime ||
                (!this.servingAgents.contains(agent) && this.servingAgents.size() >= this.maxAttendants)) {
            return true;

        } else if(!this.servingAgents.contains(agent) && this.serverPositionHandler.isAgentInside(agent)){
            this.servingAgents.add(agent);
            this.serverPositionHandler.setNewPosition(agent.getId());
            return false;
        }

        return false;
    }
}
