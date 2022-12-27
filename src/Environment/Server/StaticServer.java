package Environment.Server;

import Agent.Agent;
import Utils.Line;
import Utils.Rectangle;

public class StaticServer extends Server {
    //Static event is for an event that has a fixed schedule
    public StaticServer(int maxCapacity, Rectangle zone, double startTime, double attendingTime, Line queueLine) {
        super(maxCapacity, zone, startTime, attendingTime, queueLine);
        this.startTime = startTime;
    }


    @Override
    public Boolean canAttend(Agent agent) {
        //If event has finished, agent will be freed on next frame.
        return this.servingAgents.contains(agent);
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        return currentTime - this.startTime > this.attendingTime && agent.getStartedAttendingAt() != null;
    }
}
