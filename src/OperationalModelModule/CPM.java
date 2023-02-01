package OperationalModelModule;

import Agent.Agent;
import Agent.AgentConstants;
import CellIndexMethod.CellIndexMethod;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;
import Utils.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CPM implements OperationalModelModule {
    private final CellIndexMethod CIM;
    private final Environment environment;
    private static final double DTS_NEEDED_FOR_EXPANSION = 5;
    private static final double NEIGHBOURS_RADIUS = 5.0;
    private static final double
            ORIGINAL_DIRECTION_AP = 250,
            AGENT_AP = 100,
            AGENT_BP = 1,
            WALL_AP = 400,
            WALL_BP = 2, // cuanto mas grande, a mayor distancia reacciona mas
            AP_VARIATION = 25,
            BP_VARIATION = 0.1,
            NON_MOVING_AGENT_REPULSION_MULTIPLIER = 3;

    public CPM(Environment environment) {
        this.environment = environment;
        this.CIM = new CellIndexMethod(this.environment.getWalls(), NEIGHBOURS_RADIUS);
    }

    @Override
    public void updateAgentsPosition(List<Agent> agents) {
        this.CIM.updateAgentsPosition(agents);
    }

    private Vector calculateHeuristicDirection(Agent agent, Random random) {
        //initialize with original direction
        Vector resultantNc = agent.getVelocity().normalize()
                .scalarMultiply(getRandomDoubleInRange(ORIGINAL_DIRECTION_AP, AP_VARIATION, random));

        List<Agent> neighbours = this.CIM.getAgentNeighbours(agent);

        double AP, BP;
        for (Agent neighbour : neighbours) {
            // agent fears more those agents not moving
            if (neighbour.getVelocityModule() == 0) {
                AP = AGENT_AP * NON_MOVING_AGENT_REPULSION_MULTIPLIER;
                BP = AGENT_BP * NON_MOVING_AGENT_REPULSION_MULTIPLIER;
            } else {
                AP = AGENT_AP;
                BP = AGENT_BP;
            }

            resultantNc = resultantNc.add(
                    calculateRepulsionForce(
                            agent.getPosition(), neighbour.getPosition(), agent.getVelocity(),
                            getRandomDoubleInRange(AP, AP_VARIATION, random),
                            getRandomDoubleInRange(BP, BP_VARIATION, random)
                    )
            );
        }

        Vector closestWallPosition = this.environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
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

    public void updateNonCollisionAgent(Agent agent, double dt, Random random) {
        Vector heuristicDirection = calculateHeuristicDirection(agent, random);
        agent.setVelocity(heuristicDirection.scalarMultiply(agent.getVelocityModule()));
    }

    public void expandAgent(Agent agent) {
        if (agent.getRadius() < AgentConstants.MAX_RADIUS) {
            agent.setRadius(agent.getRadius() + AgentConstants.MAX_RADIUS / DTS_NEEDED_FOR_EXPANSION);
        }
    }

    public void updateCollidingAgents(AgentsCollision agentsCollision) {
        Agent agent1 = agentsCollision.getAgent1();
        Agent agent2 = agentsCollision.getAgent2();
        collapseAgent(agent1);
        collapseAgent(agent2);
        escapeFromObstacle(agent1, agent2.getPosition());
        escapeFromObstacle(agent2, agent1.getPosition());
    }

    public void updateWallCollidingAgent(WallCollision wallCollision) {
        collapseAgent(wallCollision.getAgent());
        escapeFromObstacle(wallCollision.getAgent(), wallCollision.getWallClosestPoint());
    }
}
