package Environment;

import Agent.Agent;
import Utils.Vector;

public class Exit implements Objective {
    private final Wall exit;

    public Exit(Wall exit) {
        this.exit = exit;
    }

    @Override
    public Vector getPosition(Agent agent) {//Devuelvo el punto del medio, esto para tratr que extienda de objective
        return this.exit.getA().add(this.exit.getB()).scalarMultiply(0.5);
    }

    @Override
    public Boolean needsAttending(Agent agent) {
        return false;
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        return true;
    }
}
