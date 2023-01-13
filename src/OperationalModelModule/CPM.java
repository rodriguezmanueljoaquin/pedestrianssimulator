package OperationalModelModule;

import Agent.Agent;
import Agent.AgentConstants;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;
import Utils.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CPM {
    private static final double EXPANSION_TIME = 0.5;
    private static final double NEIGHBOURS_RADIUS = 5.0;
    private static final double
            ORIGINAL_DIRECTION_AP = 250,
            AGENT_AP = 100,
            AGENT_BP = 0.5,
            WALL_AP = 200,
            WALL_BP = 1,
            AP_VARIATION = 25,
            BP_VARIATION = 0.1;

    public static void updateNonCollisionAgent(Agent agent, List<Agent> agents, Environment environment, double dt, Random random) {
        Vector heuristicDirection = calculateHeuristicDirection(agent, agents, environment, random);
        agent.setVelocity(heuristicDirection.scalarMultiply(agent.getVelocityModule()));
        expandAgent(agent, dt);
    }

    private static void expandAgent(Agent agent, double dt) {
        if (agent.getRadius() < AgentConstants.MAX_RADIUS) {
            agent.setRadius(agent.getRadius() + AgentConstants.MAX_RADIUS / (EXPANSION_TIME / dt));
        }
    }

    private static Vector calculateHeuristicDirection(Agent agent, List<Agent> agents, Environment environment, Random random) {
        //initialize with original direction
        Vector resultantNc = agent.getVelocity().normalize()
                .scalarMultiply(getRandomDoubleInRange(ORIGINAL_DIRECTION_AP, AP_VARIATION, random));

        // TODO: ADD CIM
        List<Agent> neighbours = agents.stream()
                .filter(other -> agent.distance(other) < NEIGHBOURS_RADIUS && !agent.equals(other))
                .collect(Collectors.toList());

        for (Agent neighbour : neighbours) {
            resultantNc = resultantNc.add(
                    calculateRepulsionForce(
                            agent.getPosition(), neighbour.getPosition(), agent.getVelocity(),
                            getRandomDoubleInRange(AGENT_AP, AP_VARIATION, random),
                            getRandomDoubleInRange(AGENT_BP, BP_VARIATION, random)
                    )
            );
        }

        Vector closestWallPosition = environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
        Vector wallRepulsion = calculateRepulsionForce(
                agent.getPosition(), closestWallPosition, agent.getVelocity(),
                getRandomDoubleInRange(WALL_AP, AP_VARIATION, random),
                getRandomDoubleInRange(WALL_BP, BP_VARIATION, random)
        );
        resultantNc = resultantNc.add(wallRepulsion);

        return resultantNc.normalize();
    }

    private static Vector calculateRepulsionForce(Vector position, Vector obstacle, Vector originalVelocity, double Ap, double Bp) {
        //eij (e sub ij)
        Vector repulsionDirection = position.substract(obstacle).normalize();

        //dij (d sub ij) distance between position and otherPosition
        Double repulsionDistance = position.distance(obstacle);

        //cos(0) = a.b / |a||b|
        double cosineOfTheta = originalVelocity.add(position).dotMultiply(obstacle) / (originalVelocity.add(position).module() * obstacle.module());

        double weight = Ap * Math.exp(-repulsionDistance / Bp);

        //eij*Ap*e^(-dij/bp)*cos(0)
        return repulsionDirection.scalarMultiply(weight * cosineOfTheta);
    }

    public static void updateCollidingAgents(AgentsCollision agentsCollision) {
        Agent agent1 = agentsCollision.getAgent1();
        Agent agent2 = agentsCollision.getAgent2();
        collapseAgent(agent1);
        collapseAgent(agent2);
        escapeFromObstacle(agent1, agent2.getPosition());
        escapeFromObstacle(agent2, agent1.getPosition());
    }

    public static void updateWallCollidingAgent(WallCollision wallCollision) {
        collapseAgent(wallCollision.getAgent());
        escapeFromObstacle(wallCollision.getAgent(), wallCollision.getWallClosestPoint());
    }

    private static void escapeFromObstacle(Agent agent, Vector other) {
        Vector oppositeDirection = other.substract(agent.getPosition()).normalize().scalarMultiply(-1.0);
        agent.setVelocity(oppositeDirection.scalarMultiply(agent.getState().getVelocity()));
    }


    private static void collapseAgent(Agent agent) {
        agent.setRadius(AgentConstants.MIN_RADIUS);
    }

    private static double getRandomDoubleInRange(double mean, double variation, Random random) {
        return mean + (random.nextDouble() - 0.5) * variation;
    }
}
