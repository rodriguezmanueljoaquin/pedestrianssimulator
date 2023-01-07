package Environment.Objectives.Server;

import Agent.Agent;
import Environment.Objectives.Objective;
import Utils.Line;
import Utils.Rectangle;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class Server implements Objective {
    protected final int maxAttendants;
    protected final List<Agent> servingAgents;
    protected final ServerPositionHandler serverPositionHandler;
    protected final QueueHandler queueHandler;
    protected final double attendingTime;
    protected Double startTime = null;

    public Server(int maxCapacity, Rectangle zone, double startTime, double attendingTime, Line queueLine) {
        this.serverPositionHandler = new ServerPositionHandler(zone);
        this.queueHandler = new QueueHandler(queueLine, this);
        this.maxAttendants = maxCapacity;
        this.attendingTime = attendingTime;
        this.servingAgents = new ArrayList<>();
    }

    public void updateServer(double currentTime) {
        List<Agent> freeAgents = new ArrayList<>();
        for (Agent servingAgent : this.servingAgents) {
            if (this.hasFinishedAttending(servingAgent, currentTime))
                freeAgents.add(servingAgent);
        }

        for (Agent agent : freeAgents) {
            freeAgent(agent);
        }

    }

    protected void freeAgent(Agent agent) {
        this.serverPositionHandler.removeAgent(agent.getId());
        this.servingAgents.remove(agent);
    }

    @Override
    public Vector getPosition(Agent agent) {
        // server positions vary per agent, as the server may command agent to go to a specific place in the queue,
        // or in the server when attending it
        if (!this.servingAgents.contains(agent))
            throw new RuntimeException("SERVER NOT ATENDING AGENT " + agent.getId());

        return this.serverPositionHandler.getOccupiedPosition(agent.getId());
    }

    @Override
    public Vector getCentroidPosition() {
        return this.serverPositionHandler.getCentroid();
    }

    public QueueHandler getQueueHandler() {
        return queueHandler;
    }
}
