package Environment.Server;

import Agent.Agent;
import Utils.Constants;
import Utils.Line;
import Utils.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class QueueHandler {
    //TODO: Esta clase, mi idea era hacer que reciba puntos de A a B donde puede crear posiciones, con la dist entre ellas.
    private final Line line;
    private final int capacity;
    private final Queue<Agent> queueingAgents;

    public QueueHandler(Line line) {
        this.line = line;
        this.capacity = line.getSegmentsQuantity();
        this.queueingAgents = new LinkedList<>();
    }

    public boolean isInQueue(Agent agent) {
        for (Agent possibleAgent : this.queueingAgents) {
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
        Agent queueingAgent = null;
        int position = 0;
        for (Agent possibleAgent : this.queueingAgents) {
            if (possibleAgent.equals(agent)) {
                queueingAgent = possibleAgent;
                break;
            }
            position++;
        }
        if (queueingAgent == null) {
            System.err.println("Agent is not in queue");
            return null;
        }

        return this.line.getSegmentPosition(position);
    }

    public void addToQueue(Agent agent) {
        if (!hasCapacity()) {
            System.out.println("No capacity in queue");
        }
        //Lo agrego igual, pero lo mando a amontonarse a B;
        this.queueingAgents.add(agent);
    }

    public Agent removeFromQueue() {
        if (this.queueingAgents.size() == 0) {
            System.err.println("No agents in queue");
            return null;
        }
        return this.queueingAgents.poll();
    }

    public int size() {
        return this.queueingAgents.size();
    }
}