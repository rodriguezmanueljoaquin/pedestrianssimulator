package Environment.Objectives.Target;


import Agent.Agent;
import Utils.Random.RandomInterface;
import Utils.Vector;
import Utils.Zone;

public class BorderTarget extends Target {
    private final Zone zone;

    public BorderTarget(RandomInterface attendingDistribution, String groupId, Zone zone) {
        super(attendingDistribution, groupId);
        this.zone = zone;
    }

    @Override
    public Vector getPosition(Agent agent) {
        return zone.getMiddlePoint();
    }

    @Override
    public Vector getCentroidPosition() {
        return zone.getMiddlePoint();
    }

    @Override
    public Boolean reachedObjective(Agent agent) {
        return zone.isPointInside(agent.getPosition());
    }
}
