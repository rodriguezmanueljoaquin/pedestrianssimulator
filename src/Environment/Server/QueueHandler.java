package Environment.Server;

import Agent.Agent;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

class QueueHandler {
    //TODO: Esta clase, mi idea era hacer que reciba puntos de A a B donde puede crear posiciones, con la dist entre ellas.
    private final Vector A, B;
    private final double spaceBetweenAgents;
    private final int capacity;
    private final List<Agent> queueingAgents;

    public QueueHandler(Vector A, Vector B, double spaceBetweenAgents){
        this.A = A;
        this.B = B;
        this.capacity = (int) (B.distance(A)/spaceBetweenAgents);
        this.spaceBetweenAgents = spaceBetweenAgents;
        queueingAgents = new ArrayList<>();
    }

    public boolean isInQueue(Agent agent){
        for(Agent possibleAgent : queueingAgents) {
            if(possibleAgent.getId() == agent.getId()){
                return true;
            }
        }
        return false;
    }
    public boolean hasCapacity(){
        return queueingAgents.size() < capacity;
    }
    public Vector getPosition(Agent agent) {
        Agent queueingAgent = null;
        int position = 0;
        for(Agent possibleAgent : queueingAgents) {
            position++;
            if(possibleAgent.getId() == agent.getId()){
                queueingAgent = possibleAgent;
                break;
            }
        }
        if(queueingAgent == null) {
            System.out.println("Agent is not in queue");
            return null;
        }
        return A.scalarMultiply(position * this.spaceBetweenAgents);

    }
    public void addToQueue(Agent agent) {
        if(!hasCapacity()) {
            System.out.println("No capacity in queue");
            return;
        }
        queueingAgents.add(agent);
    }
    public Agent removeFromQueue() {
        if(queueingAgents.size() == 0){
            System.out.println("No agents in queue");
            return null;
        }
        return queueingAgents.get(0);
    }

}