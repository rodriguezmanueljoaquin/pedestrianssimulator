package OperationalModelModule.Collisions;

import Agent.Agent;
import Environment.Environment;
import Environment.Wall;
import Utils.Vector;

import java.util.List;

public class CollisionsFinder {
    // Receives agents, environment, and the two list where it will store the collisions made by the agents
    public static void Find(List<Agent> agents, Environment environment,
                            List<WallCollision> wallCollisions, List<AgentsCollision> agentsCollisions, List<Agent> nonCollisionAgents) {
        for (int i = 0; i < agents.size(); i++) {
            Agent current = agents.get(i);
            boolean hasCollided = false;

            // wall
            Wall closestWall = environment.getClosestWall(current.getPosition());
            Vector closestPoint = closestWall.getClosestPoint(current.getPosition());
            if (current.distance(closestPoint) < 0) {
                wallCollisions.add(new WallCollision(current, closestPoint));
                hasCollided = true;
            }

            for (int j = i + 1; j < agents.size(); j++) {
                Agent other = agents.get(j);
                if (current.distance(other) <= 0) {
                    AgentsCollision newCollision = new AgentsCollision(current, other);
                    agentsCollisions.add(newCollision);
                    hasCollided = true;
                }
            }

            // chequeamos si la particula que estamos analizando esta involucrada en un choque contra otra particula
            if (agentsCollisions.stream().anyMatch(agentsCollision -> agentsCollision.getAgent2().equals(current)))
                hasCollided = true;

            if (!hasCollided)
                nonCollisionAgents.add(current);
        }
    }

}
