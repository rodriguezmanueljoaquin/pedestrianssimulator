package Environment.Objectives.Target;


import Agent.Agent;
import Environment.Objectives.Objective;
import Environment.Objectives.ObjectiveType;
import Utils.Constants;
import Utils.Vector;
import Utils.Zone;

import java.util.HashMap;
import java.util.Map;

// Dot target was not requested, so it could be erased.
public class RandomPointTarget implements Target {
    private final Zone zone;
    private final String groupId;
    private final Double attendingTime; //milliseconds needed to complete task
    private final Map<Integer, Vector> positionMap = new HashMap<>();

    public RandomPointTarget(String groupId, Zone zone, Double attendingTime) {
        this.zone = zone;
        this.attendingTime = attendingTime;
        this.groupId = groupId;
    }



    @Override
    public Vector getPosition(Agent agent) {
        if(positionMap.containsKey(agent.getId()))
            return positionMap.get(agent.getId());
        Vector position = zone.getRandomPointInside();
        positionMap.put(agent.getId(),position);
        return position;
    }

    @Override
    public Boolean hasFinishedAttending(Agent agent, double currentTime) {
        //true if the agent has started to attend and completed it "task"
        return currentTime - agent.getStartedAttendingAt() >= this.attendingTime;
    }

    @Override
    public Boolean canAttend(Agent agent) {
        return true;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.TARGET;
    }

    @Override
    public Vector getCentroidPosition() {
        return zone.getMiddlePoint();
    }

    @Override
    public Boolean reachedObjective(Agent agent) {
        //just in case should never reach here
        if(!positionMap.containsKey(agent.getId())) {
            getPosition(agent);
            return false;
        }
        return positionMap.get(agent.getId()).distance(agent.getPosition()) <= Constants.DOUBLE_EPSILON + agent.getRadius() * 2;
    }
}
