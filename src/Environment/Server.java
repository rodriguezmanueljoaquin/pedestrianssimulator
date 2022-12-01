package Environment;

import Agent.Agent;
import Utils.Vector;

import java.util.*;

public class Server implements Objective{
    //TODO: IMPLEMENT SERVERS
    private final int maxAttendants;
    private int currentAttendants;
    private static Integer count = 1;
    private final Integer id;
    private Queue<Agent> queue;
    private List<Target> targets;

    public Server(int maxCapacity) {
        this.maxAttendants = maxCapacity;
        this.currentAttendants = 0;
        this.id = count++;

        // assign
        this.queue = new LinkedList<>();
        this.targets = new ArrayList<>();
    }

    public void addToQueue(Agent agent) {
        this.queue.add(agent);
    }

    public void freeNextInQueue() {

        // emit action to freed agents, make them move to one of the targets

        queue.remove();
    }

    public boolean hasCapacity() {
        return this.currentAttendants < this.maxAttendants;
    }

    public void increaseCapacity() {
        this.currentAttendants++;
    }


    public Integer getId() {
        return this.id;
    }

    @Override
    public Vector getPosition() {
        return null;
    }

    @Override
    public Double getAttendingTime() {
        throw new RuntimeException();
    }

    @Override
    public Boolean hasAttendingTime() {
        return false;
    }

}
