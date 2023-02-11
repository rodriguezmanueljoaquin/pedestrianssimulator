package OperationalModelModule;

import Agent.Agent;
import CellIndexMethod.CellIndexMethod;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;
import Utils.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static OperationalModelModule.CPMConstants.WALL_DISTANCE_CONSIDERATION;

public class CPM implements OperationalModelModule {
    private final CellIndexMethod CIM;
    private final Environment environment;

    public CPM(Environment environment) {
        this.environment = environment;
        this.CIM = new CellIndexMethod(this.environment.getWalls(), CPMConstants.NEIGHBOURS_RADIUS);
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
        agent.setRadius(agent.getMinRadius());
    }

    private static double getRandomDoubleInRange(double mean, double variation, Random random) {
        return mean + (random.nextDouble() - 0.5) * variation;
    }

    @Override
    public void updateAgentsPosition(List<Agent> agents) {
        this.CIM.updateAgentsPosition(agents);
    }

    private Vector calculateHeuristicDirection(Agent agent, Random random) {
        //initialize with original direction
        Vector resultantNc = agent.getVelocity().normalize()
                .scalarMultiply(getRandomDoubleInRange(CPMConstants.ORIGINAL_DIRECTION_AP, CPMConstants.AP_VARIATION, random));

        List<Agent> neighbours = this.CIM.getAgentNeighbours(agent);

        double AP, BP;
        for (Agent neighbour : neighbours) {
            // agent fears more those agents not moving
            if (neighbour.getVelocityModule() == 0) {
                AP = CPMConstants.AGENT_AP * CPMConstants.NON_MOVING_AGENT_REPULSION_MULTIPLIER;
                BP = CPMConstants.AGENT_BP * CPMConstants.NON_MOVING_AGENT_REPULSION_MULTIPLIER;
            } else {
                AP = CPMConstants.AGENT_AP;
                BP = CPMConstants.AGENT_BP;
            }

            resultantNc = resultantNc.add(
                    calculateRepulsionForce(
                            agent.getPosition(), neighbour.getPosition(), agent.getVelocity(),
                            getRandomDoubleInRange(AP, CPMConstants.AP_VARIATION, random),
                            getRandomDoubleInRange(BP, CPMConstants.BP_VARIATION, random)
                    )
            );
        }
        List<Vector> closestWallsPosition = this.environment.getWalls()
                .stream()
                .map((a) -> a.getClosestPoint(agent.getPosition()))
                .filter((a) -> a.distance(agent.getPosition()) <= WALL_DISTANCE_CONSIDERATION)
                .collect(Collectors.toList());
//        Vector closestWallPosition = this.environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
        for (Vector closestWallPosition : closestWallsPosition) {
            Vector wallRepulsion = calculateRepulsionForce(
                    agent.getPosition(), closestWallPosition, agent.getVelocity(),
                    getRandomDoubleInRange(CPMConstants.WALL_AP, CPMConstants.AP_VARIATION, random),
                    getRandomDoubleInRange(CPMConstants.WALL_BP, CPMConstants.BP_VARIATION, random)
            );
            resultantNc = resultantNc.add(wallRepulsion);
        }
        return resultantNc.normalize();
    }

    public void updateNonCollisionAgent(Agent agent, double dt, Random random) {
        Vector heuristicDirection = calculateHeuristicDirection(agent, random);
        agent.setVelocity(heuristicDirection.scalarMultiply(agent.getVelocityModule()));
    }

    public void expandAgent(Agent agent) {
        if (agent.getRadius() < agent.getMaxRadius()) {
            agent.setRadius(agent.getRadius() + agent.getMaxRadius() / CPMConstants.DTS_NEEDED_FOR_EXPANSION);
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
