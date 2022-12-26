package Environment.Server;

import Agent.Agent;
import Utils.Line;
import Utils.Rectangle;
import Utils.Vector;

public class StaticServer extends Server {
    //Static event is for an event that has a fixed schedule
    public StaticServer(int maxCapacity, Rectangle zone, double startTime, double attendingTime, Line queueLine) {
        super(maxCapacity, zone, startTime, attendingTime, queueLine);
        this.startTime = startTime;
    }


    @Override
    public Boolean canAttend(Agent agent) {
        //If event has finished, agent will be freed on next frame.
        return true;
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        System.out.println("Current time: " + currentTime);
        System.out.println("Start time: " + startTime);
        System.out.println("Attending time: " + this.attendingTime);
        return currentTime - this.startTime > this.attendingTime;
    }


}
