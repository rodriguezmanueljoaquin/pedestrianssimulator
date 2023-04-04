package Environment.Objectives.Target;


import Agent.Agent;
import Utils.Constants;
import Utils.Random.RandomGenerator;
import Utils.Vector;
import Utils.Zone;

import java.util.HashMap;
import java.util.Map;

public class RandomPointTarget extends Target {
    private final Zone zone;
    private final Map<Integer, Vector> positionMap = new HashMap<>();

    public RandomPointTarget(RandomGenerator attendingDistribution, String groupId, Zone zone) {
        super(attendingDistribution, groupId);
        this.zone = zone;
    }

    @Override
    public Vector getPosition(Agent agent) {
        if (this.positionMap.containsKey(agent.getId()))
            return this.positionMap.get(agent.getId());

        Vector position = this.zone.getRandomPointInside();
        this.positionMap.put(agent.getId(), position);
        return position;
    }

    @Override
    public Vector getCentroidPosition() {
        return this.zone.getCentroid();
    }

    @Override
    public Boolean reachedObjective(Agent agent) {
        //just in case should never reach here
        if (!this.positionMap.containsKey(agent.getId())) {
            getPosition(agent);
            return false;
        }

        return agent.distance(this.positionMap.get(agent.getId())) < Constants.DOUBLE_EPSILON;
    }
}
