package Environment.Objectives.Server;

import Agent.Agent;
import Environment.Objectives.Objective;
import Environment.Objectives.ObjectiveType;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

class QueueHandler implements Objective {
    private final Queue queue;
    private final List<Agent> queueingAgents;
    private final Server server;

    public QueueHandler(Queue queue, Server server) {
        this.queue = queue;
        this.queueingAgents = new ArrayList<>();
        this.server = server;
    }

    public Vector getPosition(Agent agent) {
        // returns position designated for agent if its in queue, else returns first available position
        int spot = this.queueingAgents.indexOf(agent);
        if (spot != -1) {
            return this.queue.getSpotPosition(spot);
        }

        Vector firstAvailablePosition = this.queue.getSpotPosition(this.queueingAgents.size());
        if (agent.reachedPosition(firstAvailablePosition)) {
            // assign to queue as it got to the first free spot
            this.queueingAgents.add(agent);
        }

        return firstAvailablePosition;
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        return this.server.canAttend(agent);
    }

    @Override
    public Boolean canAttend(Agent agent) {
        // returns true when agent is in the queue and in its position
        return this.queueingAgents.contains(agent) &&
                agent.reachedPosition(this.queue.getSpotPosition(this.queueingAgents.indexOf(agent)));
    }

    public Agent removeFromQueue() {
        if (this.queueingAgents.size() == 0) {
            System.err.println("No agents in queue");
            return null;
        }
        return this.queueingAgents.remove(0);
    }

    public int agentsInQueue() {
        return this.queueingAgents.size();
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.QUEUE;
    }

    @Override
    public Vector getCentroidPosition() {
        return this.queue.getSpotPosition(0);
    }
}