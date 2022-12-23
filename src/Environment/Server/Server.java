package Environment.Server;

import Agent.Agent;
import Environment.Objective;
import Utils.Rectangle;
import Utils.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Server implements Objective {
    private final int maxAttendants;
    private final Queue<Agent> queue;
    private final List<Agent> servingAgents;
    private final ServerPositionHandler serverPositionHandler;
    private final ServingModel servingModel;
    private Double startTime = null;
    private final double attendingTime;

    public Server(int maxCapacity, Rectangle zone, ServingModel servingModel, double startTime, double attendingTime) {
        this.serverPositionHandler = new ServerPositionHandler(zone);
        this.maxAttendants = maxCapacity;
        this.servingModel = servingModel;
        if(servingModel == ServingModel.ALL_AT_ONCE)
            this.startTime = startTime;
        this.attendingTime = attendingTime;
        // assign
        this.queue = new LinkedList<>();
        this.servingAgents = new ArrayList<>();
    }


    public void addToQueue(Agent agent) {
        this.queue.add(agent);
    }

    public Agent freeNextInQueue() {
        return queue.remove();
    }

    private Vector serveNewAgent(){
        if(queue.size() == 0 || servingAgents.size() >= maxAttendants) {
            System.out.println("Queue is empty or capacity is full");
            return null;
        }
        servingAgents.add(freeNextInQueue());
        return serverPositionHandler.getNewPosition();
    }

    private class QueuePositionHandler {
        //TODO: Esta clase, mi idea era hacer que reciba puntos de A a B donde puede crear posiciones, con la dist entre ellas.
    }




    @Override
    public Vector getPosition() {
        return null;
    }

    @Override
    public Boolean hasFinishedAttending(int agentId, double startedAttendingTime, double currentTime) {
        if(this.servingModel == ServingModel.ALL_AT_ONCE
                && startTime + attendingTime > currentTime)
            return true;
        else if(this.servingModel == ServingModel.ATTENDING_TIME
                && servingAgents.get(0).getId() == agentId && startedAttendingTime + attendingTime > currentTime){
            servingAgents.remove(0);
            if(servingAgents.size() > 0)
                servingAgents.get(0).setStartedAttendingAt(currentTime);

            return true;
        }

        return false;
    }

    @Override
    public Boolean hasToAttend() {
        return false;
    }

    @Override
    public Boolean isServer() {
        return true;
    }
}
