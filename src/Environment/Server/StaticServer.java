package Environment.Server;

import Agent.Agent;
import Utils.Rectangle;
import Utils.Vector;

public class StaticServer extends Server {
    //Static event is for an event that has a fixed schedule
    public StaticServer(int maxCapacity, Rectangle zone, double startTime, double attendingTime, Vector A, Vector B) {
        super(maxCapacity, zone, startTime, attendingTime, A, B);
        this.startTime = startTime;
    }


    @Override
    public Boolean needsAttending(Agent agent) {
        //If event has finished, agent will be freed on next frame.
        return true;
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        return currentTime - startTime > attendingTime;
    }


}
