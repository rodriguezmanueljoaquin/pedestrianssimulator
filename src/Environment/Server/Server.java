package Environment.Server;

import Agent.Agent;
import Environment.Objective;
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

    public Server(int maxCapacity, Rectangle zone, double startTime, double attendingTime, Vector A, Vector B) {
        this.serverPositionHandler = new ServerPositionHandler(zone);
        this.queueHandler = new QueueHandler(A, B);
        this.maxAttendants = maxCapacity;
        this.attendingTime = attendingTime;
        // assign
        this.servingAgents = new ArrayList<>();
    }

    public void updateServer() {
        Agent agent;
        while (servingAgents.size() <= maxAttendants && queueHandler.size() > 0) {
            agent = queueHandler.removeFromQueue();
            servingAgents.add(agent);
            serverPositionHandler.setNewPosition(agent.getId());
        }
    }

    @Override
    public Vector getPosition(Agent agent) {
        // server positions vary per agent, as the server may command agent to go to a specific place  in the queue,
        // or in the server when attending it
        if (serverPositionHandler.isInServer(agent.getId()))
            return serverPositionHandler.getOccupiedPosition(agent.getId());

        if (queueHandler.isInQueue(agent))
            return queueHandler.getPosition(agent);

        if (queueHandler.size() != 0 || servingAgents.size() >= maxAttendants) {
            queueHandler.addToQueue(agent);
            return queueHandler.getPosition(agent);
        }

        servingAgents.add(agent);
        return serverPositionHandler.setNewPosition(agent.getId());
    }

    @Override
    public Boolean isServer() {
        return true;
    }
}
