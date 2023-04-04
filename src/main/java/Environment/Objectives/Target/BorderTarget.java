package Environment.Objectives.Target;


import Agent.Agent;
import Utils.Random.RandomGenerator;
import Utils.Vector;
import Utils.Zone;

public class BorderTarget extends Target {
    private final Zone zone;

    public BorderTarget(RandomGenerator attendingDistribution, String groupId, Zone zone) {
        super(attendingDistribution, groupId);
        this.zone = zone;
    }

    @Override
    public Vector getPosition(Agent agent) {
        return zone.getCentroid();
    }

    @Override
    public Vector getCentroidPosition() {
        return zone.getCentroid();
    }

    @Override
    public Boolean reachedObjective(Agent agent) {
        return zone.isPointInside(agent.getPosition());
    }
}
