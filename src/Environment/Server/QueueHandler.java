package Environment.Server;

import Agent.Agent;
import Utils.Constants;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

class QueueHandler {
    //TODO: Esta clase, mi idea era hacer que reciba puntos de A a B donde puede crear posiciones, con la dist entre ellas.
    private final Vector A, B;
    private final int capacity;
    private final List<Agent> queueingAgents;

    public QueueHandler(Vector A, Vector B) {
        this.A = A;
        this.B = B;
        this.capacity = (int) (this.B.distance(this.A) / Constants.SPACE_IN_QUEUE);
        System.out.println(this.B.distance(this.A));
        System.out.println(capacity);
        queueingAgents = new ArrayList<>();
    }

    public boolean isInQueue(Agent agent) {
        for (Agent possibleAgent : queueingAgents) {
            if (possibleAgent.getId() == agent.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCapacity() {
        return queueingAgents.size() < capacity;
    }

    public Vector getPosition(Agent agent) {
        Agent queueingAgent = null;
        int position = 0;
        for (Agent possibleAgent : queueingAgents) {
            position++;
            if (possibleAgent.getId() == agent.getId()) {
                queueingAgent = possibleAgent;
                break;
            }
        }
        if (queueingAgent == null) {
            System.out.println("Agent is not in queue");
            return null;
        }
        if (position > capacity)
            return B;
        return this.A.scalarMultiply(Constants.SPACE_IN_QUEUE * (capacity - position)).add(B.scalarMultiply(Constants.SPACE_IN_QUEUE * (position)));

    }

    public void addToQueue(Agent agent) {
        if (!hasCapacity()) {
            System.out.println("No capacity in queue");
        }
        //Lo agrego igual, pero lo mando a amontonarse a B;
        queueingAgents.add(agent);
    }

    public Agent removeFromQueue() {
        if (queueingAgents.size() == 0) {
            System.out.println("No agents in queue");
            return null;
        }
        return queueingAgents.remove(0);
    }

    public int size() {
        return queueingAgents.size();
    }
}