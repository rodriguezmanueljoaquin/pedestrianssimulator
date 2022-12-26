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
    private final Line line;
    private final int capacity;
    private final List<Agent> queueingAgents;
    private Boolean hasToUpdate;

    public QueueHandler(Line line) {
        this.line = line;
        this.capacity = line.getSegmentsQuantity();
        this.queueingAgents = new LinkedList<>();
        this.hasToUpdate = true;
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
        int position = 0;
        for (Agent possibleAgent : this.queueingAgents) {
            if(possibleAgent.getPosition().distance(this.line.getSegmentPosition(position)) > 2*Constants.SPACE_BETWEEN_AGENTS_IN_QUEUE)
                this.hasToUpdate = true;
            if (possibleAgent.equals(agent)) {
                return this.line.getSegmentPosition(position);
            }
            position++;
        }
        System.err.println("Agent is not in queue");
        return null;
    }

    public void addToQueue(Agent agent) {
        if (!hasCapacity()) {
            System.out.println("No capacity in queue");
        }
        //Lo agrego igual, pero lo mando a amontonarse a B;
        this.queueingAgents.add(agent);
    }

    public void updateQueue(){
        if(!hasToUpdate)
            return;
        Agent minDistanceAgent;
        int minPosition;
        for(int i =0;i < queueingAgents.size();i++){
            Vector positionInQueue = this.line.getSegmentPosition(i);
            minDistanceAgent = queueingAgents.get(i);
            minPosition = i;
            Double minDistance = queueingAgents.get(i).getPosition().distance(positionInQueue);
            for(int j =i + 1;j < queueingAgents.size();j++) {
                if(minDistance > queueingAgents.get(j).getPosition().distance(positionInQueue)) {
                    minDistanceAgent = queueingAgents.get(j);
                    minDistance = queueingAgents.get(j).getPosition().distance(positionInQueue);
                    minPosition = j;
                }
            }
            queueingAgents.set(minPosition,queueingAgents.get(i));
            queueingAgents.set(i,minDistanceAgent);
        }
        hasToUpdate = false;
    }

    public Agent removeFromQueue() {
        if (this.queueingAgents.size() == 0) {
            System.err.println("No agents in queue");
            return null;
        }
        return this.queueingAgents.remove(0);
    }

    public int size() {
        return this.queueingAgents.size();
    }
}