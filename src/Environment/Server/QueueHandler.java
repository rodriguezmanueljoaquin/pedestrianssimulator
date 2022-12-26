package Environment.Server;

import Agent.Agent;
import Environment.Objective;
import Utils.Constants;
import Utils.Line;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

class QueueHandler implements Objective {
    private final Line line;
    private final List<Agent> queueingAgents;
    private final Server server;

    public QueueHandler(Line line, Server server) {
        this.line = line;
        this.queueingAgents = new ArrayList<>();
        this.server = server;
    }

    public Vector getPosition(Agent agent) {
        // returns position designated for agent if its in queue, else returns first available position
        int position = this.queueingAgents.indexOf(agent);
        if (position != -1) {
            return this.line.getSegmentPosition(position);
        }

        Vector firstAvailablePosition = this.line.getSegmentPosition(this.queueingAgents.size() - 1);
        if (agent.getPosition().distance(firstAvailablePosition) < Constants.MINIMUM_DISTANCE_TO_TARGET) {
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
                agent.getPosition().distance(this.line.getSegmentPosition(this.queueingAgents.indexOf(agent))) < Constants.MINIMUM_DISTANCE_TO_TARGET;
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
    public Boolean isQueue() {
        return true;
    }
}