package Environment.Server;

import Agent.Agent;
import Environment.Objective;
import Utils.Rectangle;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class Server implements Objective {
    private final int maxAttendants;
    private final List<Agent> servingAgents;
    private final ServerPositionHandler serverPositionHandler;
    private final QueueHandler queueHandler;
    private final ServingModel servingModel;
    private Double startTime = null;
    private final double attendingTime;

    public Server(int maxCapacity, Rectangle zone, ServingModel servingModel, double startTime, double attendingTime,Vector A, Vector B,Double spaceBetweenAgents) {
        this.serverPositionHandler = new ServerPositionHandler(zone);
        this.queueHandler = new QueueHandler(A,B,spaceBetweenAgents);
        this.maxAttendants = maxCapacity;
        this.servingModel = servingModel;
        if(servingModel == ServingModel.ALL_AT_ONCE)
            this.startTime = startTime;
        this.attendingTime = attendingTime;
        // assign
        this.servingAgents = new ArrayList<>();
    }

    private void serveNewAgent(){
        if(servingAgents.size() >= maxAttendants) {
            System.out.println("Capacity is full");
            return;
        }
        Agent agent = queueHandler.removeFromQueue();
        servingAgents.add(agent);
        serverPositionHandler.setNewPosition(agent.getId());
    }
    @Override
    public Vector getPosition(Agent agent) {
        //Cuando me llaman al getPosition, basicamente estoy en esta situaccion:
        //Tengo el objetivo del server y tengo que ir a algun lugar, ahora...
        //adonde voy? -> Primero me fijo, ya estoy en el sistema?
        //sino: me fijo, hay capacidad? si la hay -> me voy al server directo
        //sino me agrego a la queue
        if(serverPositionHandler.isInServer(agent.getId()))
            return serverPositionHandler.getOccupiedPosition(agent.getId());
        if(queueHandler.isInQueue(agent))
            return queueHandler.getPosition(agent);
        if(servingAgents.size() > maxAttendants){
            queueHandler.addToQueue(agent);
            return queueHandler.getPosition(agent);
        }
        servingAgents.add(agent);
        return serverPositionHandler.setNewPosition(agent.getId());
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double startedAttendingTime, double currentTime) {
        if(this.servingModel == ServingModel.ALL_AT_ONCE
                && startTime + attendingTime > currentTime)
            return true;
        else if(this.servingModel == ServingModel.ATTENDING_TIME
                && servingAgents.get(0).getId() == agent.getId() && startedAttendingTime + attendingTime > currentTime){
            servingAgents.remove(0);
            serverPositionHandler.removeAgent(0);
            return true;
        }

        return false;
    }

    @Override
    public Boolean hasToAttend(Agent agent) {
        if(servingAgents.size() == 0)
            return false;

        if(this.servingModel == ServingModel.ALL_AT_ONCE) {
            return true;
        }
        return this.servingAgents.get(0).getId() == agent.getId();
        //Lo inicializo en hasFinishedAttending asi no me tienen que pasar el currentTime aca tmbn.
    }

    @Override
    public Boolean isServer() {
        return true;
    }
}
