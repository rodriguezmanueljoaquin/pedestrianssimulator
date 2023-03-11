package Environment.Objectives.Target;


import Agent.Agent;
import Utils.Constants;
import Utils.Random.RandomGenerator;
import Utils.Vector;
import Utils.Zone;

public class DotTarget extends Target {
    private final Zone zone;

    public DotTarget(RandomGenerator attendingDistribution, String groupId, Zone zone) {
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
        return agent.distance(zone.getMiddlePoint()) < Constants.DOUBLE_EPSILON;
    }
}
