package Environment;

import Utils.Vector;

public class Exit extends Objective {
    private final Wall exit;

    public Exit(Wall exit) {
        this.exit = exit;
    }

    @Override
    public Vector getPosition() {//Devuelvo el punto del medio, esto para tratr que extienda de objective
        return this.exit.getA().add(this.exit.getB()).scalarMultiply(0.5);
    }

    @Override
    public Double getAttendingTime() {
        return null;
    }

    @Override
    public Boolean hasAttendingTime() {
        return false;
    }
}
