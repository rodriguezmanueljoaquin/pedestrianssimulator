package Environment.Server;

import Agent.Agent;
import Environment.Objective;
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
    protected Double startTime = null;
    protected final double attendingTime;

    public Server(int maxCapacity, Rectangle zone, double startTime, double attendingTime, Line queueLine) {
        this.serverPositionHandler = new ServerPositionHandler(zone);
        this.queueHandler = new QueueHandler(queueLine);
        this.maxAttendants = maxCapacity;
        this.attendingTime = attendingTime;
        this.servingAgents = new ArrayList<>();
    }

    public void updateServer() {
        Agent agent;
        queueHandler.updateQueue();
        while (this.servingAgents.size() < this.maxAttendants && this.queueHandler.size() > 0) {
            agent = queueHandler.removeFromQueue();
            this.startAttendingAgent(agent);
        }
    }

    private Vector startAttendingAgent(Agent agent) {
        this.servingAgents.add(agent);
        return this.serverPositionHandler.setNewPosition(agent.getId());
    }

    protected void freeAgent(Agent agent) {
        this.serverPositionHandler.removeAgent(agent.getId());
        this.servingAgents.remove(agent);
    }

    @Override
    public Vector getPosition(Agent agent) {
        // server positions vary per agent, as the server may command agent to go to a specific place in the queue,
        // or in the server when attending it
        if (this.servingAgents.contains(agent))
            return this.serverPositionHandler.getOccupiedPosition(agent.getId());

        if (this.queueHandler.isInQueue(agent))
            return this.queueHandler.getPosition(agent);

        if (this.queueHandler.size() != 0 || this.servingAgents.size() >= this.maxAttendants) {
            this.queueHandler.addToQueue(agent);
            return this.queueHandler.getPosition(agent);
        }

        return this.startAttendingAgent(agent);
    }

    @Override
    public Boolean isServer() {
        return true;
    }
}
