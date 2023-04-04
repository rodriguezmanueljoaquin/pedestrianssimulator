package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;
import Utils.Vector;
import org.apache.commons.math3.util.FastMath;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CPMAnisotropic extends CPM {
    public CPMAnisotropic(Environment environment, double agentsMaximumMostPossibleRadius, double dt) {
        super(environment, agentsMaximumMostPossibleRadius, dt);
    }

    private static double getBeta(Agent agent, Agent otherAgent) {
        //As PDF says, Beta = the angle formed by speed of agent and the relative direction between other agent and agent
        Vector relativeDirection = otherAgent.getPosition().subtract(agent.getPosition());
        Vector agentDirection = agent.getDirection();
        // We use acos to calculate this direction
        return FastMath.acos(agentDirection.dotMultiply(relativeDirection) / (agentDirection.module() * relativeDirection.module()));
    }

    private static boolean parallelLinesIntersectAgentRadiusAndDistanceIsValid(Agent agent, Agent otherAgent) {
//        https://math.stackexchange.com/questions/422602/convert-two-points-to-line-eq-ax-by-c-0
//        https://math.stackexchange.com/questions/1481904/distance-between-line-and-circle
        Vector point1 = agent.getPosition();
        Vector point2 = agent.getPosition().add(agent.getDirection());
        Vector circleCenter = otherAgent.getPosition();
        double circleRadius = otherAgent.getRadius();
        return distanceLineFromCircle(point1, point2, circleCenter) - agent.getRadius() - circleRadius < 0;
    }

    private static double distanceLineFromCircle(Vector point1, Vector point2, Vector circleCenter) {
        double A = point1.getY() - point2.getY();
        double B = point2.getX() - point1.getX();
        double C = point1.getX() * point2.getY() - point2.getX() * point1.getY();
        return Math.abs(A * circleCenter.getX() + B * circleCenter.getY() + C) / Math.sqrt(A * A + B * B);
    }

    @Override
    public boolean agentCollidesAgainst(Agent agent, Agent otherAgent) {
        if (agent.getRadius() == agent.getMinRadius()) {
            // like CPM
            return agent.distance(otherAgent) < 0;
        } else {
            double beta = getBeta(agent, otherAgent);
            return beta >= 0 && beta <= Math.PI / 2 &&
                    agent.distance(otherAgent) < agent.getRadius() &&
                    parallelLinesIntersectAgentRadiusAndDistanceIsValid(agent, otherAgent);
        }
    }

    @Override
    public void findCollisions(List<Agent> agents, Environment environment, List<WallCollision> wallCollisions, List<AgentsCollision> agentsCollisions, List<Agent> nonCollisionAgents) {
        for (int i = 0; i < agents.size(); i++) {
            Agent current = agents.get(i);
            boolean hasCollided = findWallCollision(current, environment, wallCollisions) ||
                    // we may consider X agent collided against Y agent, but Y agent have not collided against X agent
                    findAgentCollision(current,
                            Stream.concat(
                                            agents.subList(0, i).stream(),
                                            agents.subList(i + 1, agents.size()).stream())
                                    .collect(Collectors.toList()),
                            agentsCollisions);

            if (!hasCollided)
                nonCollisionAgents.add(current);
        }
    }

    @Override
    public void updateCollidingAgents(AgentsCollision agentsCollision) {
        Agent agent1 = agentsCollision.getAgent1();
        Agent agent2 = agentsCollision.getAgent2();
        collapseAgent(agent1);
        escapeFromObstacle(agent1, agent2.getPosition());

        saveAgentDirection(agent1);
    }

}
