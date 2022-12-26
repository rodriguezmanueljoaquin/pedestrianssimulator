package Environment.Server;

import Agent.Agent;
import Utils.Constants;
import Utils.Line;
import Utils.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class QueueHandler {
    private final Line line;
    private final int capacity;
    private final List<Agent> queueingAgents;
    private final List<Agent> incomingAgentsToQueue;

    public QueueHandler(Line line) {
        this.line = line;
        this.capacity = line.getSegmentsQuantity();
        this.queueingAgents = new ArrayList<>();
        this.incomingAgentsToQueue = new ArrayList<>();
    }

    public boolean isInQueueOrGoingToQueue(Agent agent) {
        for (Agent possibleAgent : this.queueingAgents) {
            if (possibleAgent.equals(agent)) {
                return true;
            }
        }
        for (Agent possibleAgent : this.incomingAgentsToQueue) {
            if (possibleAgent.equals(agent)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCapacity() {
        return this.queueingAgents.size() < this.capacity;
    }

    public Vector getPosition(Agent agent) {
        if(this.incomingAgentsToQueue.contains(agent)) {
            Vector firstAvailablePosition = this.line.getSegmentPosition(this.queueingAgents.size() -1);
            if(agent.getPosition().distance(firstAvailablePosition) < Constants.MINIMUM_DISTANCE_TO_TARGET) {
                // assign to queue as it got to the first free spot
                this.queueingAgents.add(agent);
                this.incomingAgentsToQueue.remove(agent);
            }

            return firstAvailablePosition;

        } else {
            int position = this.queueingAgents.indexOf(agent);
            if(position == -1) {
                System.err.println("Agent is not in queue neither going to queue");
                return null;
            }

            return this.line.getSegmentPosition(position);
        }
    }

    public void addToAgentsGoingToQueue(Agent agent) {
        if (!hasCapacity()) {
            System.out.println("No capacity in queue");
        }
        //Lo agrego igual, pero lo mando a amontonarse a B;
        this.incomingAgentsToQueue.add(agent);
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
}