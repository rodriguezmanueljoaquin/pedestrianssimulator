package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import Environment.Wall;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;
import Utils.Vector;

import java.util.List;

public class CPMAnisotropic extends CPM {
    public CPMAnisotropic(Environment environment, double agentsMaximumMostPossibleRadius, double dt) {
        super(environment, agentsMaximumMostPossibleRadius, dt);
    }

    private static double getBeta(Agent agent, Agent otherAgent) {
        //As PDF says, Beta = the angle formed by speed of agent and the relative direction between other agent and agent
        Vector relativeDirection = otherAgent.getPosition().subtract(agent.getPosition());
        Vector agentDirection = agent.getDirection();
        // We use acos to calculate this direction
        return Math.acos(agentDirection.dotMultiply(relativeDirection) / (agentDirection.module() * relativeDirection.module()));
    }

    public static boolean isInContactWithAgent(Agent agent, Agent otherAgent) {
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

    public static boolean parallelLinesIntersectAgentRadiusAndDistanceIsValid(Agent agent, Agent otherAgent) {
//        https://math.stackexchange.com/questions/422602/convert-two-points-to-line-eq-ax-by-c-0
//        https://math.stackexchange.com/questions/1481904/distance-between-line-and-circle
//        Aca, en vez de calcular para las dos lineas paralelas me fijo si la distancia
//        Entre el circulo y la linea menos el radio del agente es menor que 0
//        Es distinto a lo que dice el paper asi que checkear con Rafa
//        Revisar esto y ver como hago para sacar el punto P que es el mas cercano, asi lo comparo con
//        la distancia al centro del agente y me fijo que sea menor a 2*ri que es la segunda condicion del Ademas
//        SI REVERTIMOS LAS CONDICIONES, LEER EL EXTRA, solo deberia llamar esta funcion si el radio es menor a 2*ri
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
    public void updateCollidingAgents(AgentsCollision agentsCollision) {
        Agent agent1 = agentsCollision.getAgent1();
        Agent agent2 = agentsCollision.getAgent2();
        if (isInContactWithAgent(agent1, agent2)) {
            saveAgentDirection(agent1);
            collapseAgent(agent1);
            escapeFromObstacle(agent1, agent2.getPosition());
        }
        if (isInContactWithAgent(agent2, agent1)) {
            collapseAgent(agent2);
            escapeFromObstacle(agent2, agent1.getPosition());
            saveAgentDirection(agent2);
        }
    }

    @Override
    public void findCollisions(List<Agent> agents, Environment environment, List<WallCollision> wallCollisions, List<AgentsCollision> agentsCollisions, List<Agent> nonCollisionAgents) {
        for (int i = 0; i < agents.size(); i++) {
            Agent current = agents.get(i);
            boolean hasCollided = findWallCollision(current, environment, wallCollisions);

            for (int j = i + 1; j < agents.size(); j++) {
                Agent other = agents.get(j);
                if (isInContactWithAgent(current, other)) {
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
