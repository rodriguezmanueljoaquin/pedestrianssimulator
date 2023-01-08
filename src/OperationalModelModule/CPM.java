package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import Utils.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class CPM {
    private static final double MAX_RADIUS = 5;
    private static final double MIN_RADIUS = 1;
    private static final double EXPANSION_TIME = 0.5;
    private static final double MAX_SPEED = 3.0;
    private static final double NEIGHBOURS_RADIUS = 3.0;
    private static final double ORIGINAL_DIRECTION_AP = 200, AGENT_AP = 400, AGENT_BP = 0.5, WALL_AP = 100, WALL_BP = 0.5, BETA = .9, TAU = .5;
    // TODO: MAPA<ID DE AGENTE, CPMAGENT> PARA ASOCIAR COEFICIENTES DISTINTOS (randomizados un poco desde un valor) A LOS AGENTS

    public static void updateAgent(Agent agent, List<Agent> agents, Environment environment) {
        Vector heuristicDirection = calculateHeuristicDirection(agent, agents, environment);
        agent.setVelocity(heuristicDirection.scalarMultiply(agent.getState().getVelocity()));
    }

    private static Vector calculateHeuristicDirection(Agent agent, List<Agent> agents, Environment environment) {
        Vector resultantNc = agent.getVelocity().normalize().scalarMultiply(ORIGINAL_DIRECTION_AP); //initialize with original direction

        List<Agent> neighbours = agents.stream()
                .filter(other -> agent.getPosition().distance(other.getPosition()) < NEIGHBOURS_RADIUS && !agent.equals(other))
                .collect(Collectors.toList());

        for (Agent neighbour : neighbours) {
            resultantNc = resultantNc.add(calculateRepulsionForce(agent.getPosition(), neighbour.getPosition(), agent.getVelocity(), AGENT_AP, AGENT_BP));
        }

        Vector closestWallPosition = environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
        Vector wallRepulsion = calculateRepulsionForce(agent.getPosition(),closestWallPosition,agent.getVelocity(), WALL_AP, WALL_BP);
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

}
