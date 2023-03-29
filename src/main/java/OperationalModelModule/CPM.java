package OperationalModelModule;

import Agent.Agent;
import CellIndexMethod.CellIndexMethod;
import Environment.Environment;
import Environment.Wall;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;
import Utils.Vector;

import java.util.*;
import java.util.stream.Collectors;

import static OperationalModelModule.CPMConstants.WALL_DISTANCE_CONSIDERATION;

public class CPM implements OperationalModelModule {
    protected final CellIndexMethod CIM;
    protected final Environment environment;
    protected final Map<Integer, Vector> agentsPreviousDirection;
    private final double radiusIncrementCoefficient;

    public CPM(Environment environment, double agentsMaximumMostPossibleRadius, double dt) {
        this.environment = environment;
        this.CIM = new CellIndexMethod(this.environment.getWalls(), CPMConstants.NEIGHBOURS_RADIUS, agentsMaximumMostPossibleRadius);
        this.agentsPreviousDirection = new HashMap<>();
        this.radiusIncrementCoefficient = CPMConstants.TAU / dt;
    }

    static Vector calculateRepulsionForce(Vector position, Vector obstacle, Vector originalDirection, double Ap, double Bp) {
        //eij (e sub ij)
        Vector repulsionDirection = position.subtract(obstacle).normalize();

        //dij (d sub ij) distance between position and otherPosition
        Double repulsionDistance = position.distance(obstacle);

        //cos(0) = a.b / |a||b|
        double cosineOfTheta = originalDirection.add(position).dotMultiply(obstacle) / (originalDirection.add(position).module() * obstacle.module());

        double weight = Ap * Math.exp(-repulsionDistance / Bp);

        //eij*Ap*e^(-dij/bp)*cos(0)
        return repulsionDirection.scalarMultiply(weight * cosineOfTheta);
    }

    static void escapeFromObstacle(Agent agent, Vector other) {
        Vector oppositeDirection = agent.getPosition().subtract(other).normalize();
        agent.setDirection(oppositeDirection);
    }

    static void collapseAgent(Agent agent) {
        agent.setRadius(agent.getMinRadius());
    }

    protected static double getRandomDoubleInRange(double mean, double variation, Random random) {
        return mean + (random.nextDouble() - 0.5) * variation;
    }

    @Override
    public void updateAgents(List<Agent> agents) {
        this.CIM.updateAgentsPosition(agents);
        // remove from map those agent that left
        List<Integer> agentsIdToRemove = new ArrayList<>();
        for (Integer agentId : this.agentsPreviousDirection.keySet()) {
            if (agents.stream().noneMatch(a -> Objects.equals(a.getId(), agentId)))
                agentsIdToRemove.add(agentId);
        }
        agentsIdToRemove.forEach(this.agentsPreviousDirection::remove);
    }

    protected Vector calculateHeuristicDirection(Agent agent, Random random) {
        Vector resultantDirection = new Vector(0, 0);
        //initialize with original direction if exists
        if (this.agentsPreviousDirection.containsKey(agent.getId())) {
            resultantDirection = resultantDirection.add(this.agentsPreviousDirection.get(agent.getId())
                    .scalarMultiply(getRandomDoubleInRange(CPMConstants.ORIGINAL_DIRECTION_AP, CPMConstants.AP_VARIATION, random)));
        }

        //add new direction
        resultantDirection = resultantDirection.add(agent.getDirection()
                .scalarMultiply(getRandomDoubleInRange(CPMConstants.NEW_DIRECTION_AP, CPMConstants.AP_VARIATION, random)));

        resultantDirection = resultantDirection.add(calculateAgentRepulsion(agent, random));
        resultantDirection = resultantDirection.add(calculateWallRepulsion(agent, random));
        return resultantDirection.normalize();
    }

    protected Vector calculateAgentRepulsion(Agent agent, Random random) {
        List<Agent> neighbours = this.CIM.getAgentNeighbours(agent);
        Vector agentsRepulsion = new Vector(0, 0);

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

            agentsRepulsion = agentsRepulsion.add(
                    calculateRepulsionForce(
                            agent.getPosition(), neighbour.getPosition(), agent.getDirection(),
                            getRandomDoubleInRange(AP, CPMConstants.AP_VARIATION, random),
                            getRandomDoubleInRange(BP, CPMConstants.BP_VARIATION, random)
                    )
            );
        }
        return agentsRepulsion;
    }

    // considers all walls nearby
    protected Vector calculateWallsRepulsion(Agent agent, Random random) {
        Vector wallsRepulsion = new Vector(0, 0);
        List<Vector> closestWallsPosition = this.environment.getWalls()
                .stream()
                .map((a) -> a.getClosestPoint(agent.getPosition()))
                .filter((a) -> a.distance(agent.getPosition()) <= WALL_DISTANCE_CONSIDERATION)
                .collect(Collectors.toList());

        for (Vector closestWallPosition : closestWallsPosition) {
            Vector wallRepulsion = calculateRepulsionForce(
                    agent.getPosition(), closestWallPosition, agent.getVelocity(),
                    getRandomDoubleInRange(CPMConstants.CLOSEST_WALLS_AP, CPMConstants.AP_VARIATION, random),
                    getRandomDoubleInRange(CPMConstants.CLOSEST_WALLS_BP, CPMConstants.BP_VARIATION, random)
            );
            wallsRepulsion = wallsRepulsion.add(wallRepulsion);
        }

        return wallsRepulsion;
    }

    // considers the closest wall
    protected Vector calculateWallRepulsion(Agent agent, Random random) {
        Optional<Vector> closestWallPosition = this.environment.getWalls()
                .stream()
                .map((a) -> a.getClosestPoint(agent.getPosition()))
                .filter((a) -> a.distance(agent.getPosition()) <= WALL_DISTANCE_CONSIDERATION)
                .min(Comparator.comparingDouble(o -> o.distance(agent.getPosition())));

        if (closestWallPosition.isPresent()) {
            return calculateRepulsionForce(
                    agent.getPosition(), closestWallPosition.get(), agent.getDirection(),
                    getRandomDoubleInRange(CPMConstants.CLOSEST_WALL_AP, CPMConstants.AP_VARIATION, random),
                    getRandomDoubleInRange(CPMConstants.CLOSEST_WALL_BP, CPMConstants.BP_VARIATION, random)
            );
        } else return new Vector(0, 0);
    }

    protected boolean findWallCollision(Agent agent, Environment environment, List<WallCollision> wallCollisions) {
        Wall closestWall = environment.getClosestWall(agent.getPosition());
        Vector closestPoint = closestWall.getClosestPoint(agent.getPosition());
        if (agent.distance(closestPoint) < 0) {
            wallCollisions.add(new WallCollision(agent, closestPoint));
            return true;
        }
        return false;
    }

    @Override
    public void findCollisions(List<Agent> agents, Environment environment, List<WallCollision> wallCollisions, List<AgentsCollision> agentsCollisions, List<Agent> nonCollisionAgents) {
        for (int i = 0; i < agents.size(); i++) {
            Agent current = agents.get(i);
            boolean hasCollided = findWallCollision(current, environment, wallCollisions);

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

    public void updateNonCollisionAgent(Agent agent, Random random) {
        Vector heuristicDirection = calculateHeuristicDirection(agent, random);
        agent.setDirection(heuristicDirection);
        saveAgentDirection(agent);
    }

    public void expandAgent(Agent agent) {
        if (agent.getRadius() < agent.getMaxRadius()) {
            agent.setRadius(agent.getRadius() + ((agent.getMaxRadius() - agent.getMinRadius()) / (this.radiusIncrementCoefficient)));
        }
    }

    public void updateCollidingAgents(AgentsCollision agentsCollision) {
        Agent agent1 = agentsCollision.getAgent1();
        Agent agent2 = agentsCollision.getAgent2();
        collapseAgent(agent1);
        collapseAgent(agent2);
        escapeFromObstacle(agent1, agent2.getPosition());
        escapeFromObstacle(agent2, agent1.getPosition());

        saveAgentDirection(agent1);
        saveAgentDirection(agent2);
    }

    public void updateWallCollidingAgent(WallCollision wallCollision) {
        collapseAgent(wallCollision.getAgent());
        escapeFromObstacle(wallCollision.getAgent(), wallCollision.getWallClosestPoint());
        saveAgentDirection(wallCollision.getAgent());
    }

    @Override
    public void executeOperationalModelModule(List<Agent> agents, Environment environment, Random random) {
        this.updateAgents(agents);
        List<WallCollision> wallCollisions = new ArrayList<>();
        List<AgentsCollision> agentsCollisions = new ArrayList<>();
        List<Agent> nonCollisionAgents = new ArrayList<>();
        this.findCollisions(agents, environment, wallCollisions, agentsCollisions, nonCollisionAgents);

        for (AgentsCollision agentsCollision : agentsCollisions) {
            this.updateCollidingAgents(agentsCollision);
        }

        for (WallCollision wallCollision : wallCollisions) {
            this.updateWallCollidingAgent(wallCollision);
            Agent agent = wallCollision.getAgent();
            agent.getStateMachine().updateAgentCurrentPath(agent); // maybe because of this impact it has to change its path
        }

        for (Agent agent : nonCollisionAgents) {
            // update radius
            this.expandAgent(agent);

            if (agent.getState().getMaxVelocityFactor() != 0)
                // if moving, update direction with heuristics
                this.updateNonCollisionAgent(agent, random);
        }
    }

    protected void saveAgentDirection(Agent agent) {
        this.agentsPreviousDirection.put(agent.getId(), agent.getDirection());
    }
}
