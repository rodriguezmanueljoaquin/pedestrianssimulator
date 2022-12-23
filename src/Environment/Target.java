package Environment;


import Utils.Vector;

public class Target implements Objective {
    private final Vector position;
    private final Integer id;
    private final Double attendingTime; //milliseconds needed to complete task

    // should have information about if it is occupied?

    public Target(Integer id, Vector position, Double attendingTime) {
        this.position = position;
        this.attendingTime = attendingTime;
        this.id = id;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public Boolean hasFinishedAttending(int agentId, double startedAttendingTime, double currentTime) {
        return currentTime - startedAttendingTime >= this.attendingTime;
    }

    @Override
    public Boolean hasToAttend() {
        return true;
    }

}
