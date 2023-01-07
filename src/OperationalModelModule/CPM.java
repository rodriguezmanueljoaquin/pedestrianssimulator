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
    private static final double HEURISTIC_WEIGHT = 0.4;
    private static final double agentAp = 1200, agentBp = 0.6, wallAp = 100, wallBp = 0.1, beta = .9, tau = .5;
    // TODO: MAPA<ID DE AGENTE, CPMAGENT> PARA ASOCIAR COEFICIENTES DISTINTOS (randomizados un poco desde un valor) A LOS AGENTS

    public static void updateAgent(Agent agent, List<Agent> agents, Environment environment) {
        Vector heuristicVelocity = calculateHeuristicVelocity(agent, agents, environment);
        Vector resultantVelocity =
                calculateTargetDirection(heuristicVelocity, agent.getVelocity()).scalarMultiply(agent.getState().getVelocity()); // takes in account both heuristic velocity and original agent velocity
//        if(heuristicVelocity.module() != 0.0)
//            System.out.println(heuristicVelocity);
        agent.setVelocity(resultantVelocity);
    }

    private static Vector calculateHeuristicVelocity(Agent agent, List<Agent> agents, Environment environment) {
        Vector resultantNc = new Vector(0, 0);
        List<Agent> neighbours = agents.stream()
                .filter(other -> agent.getPosition().distance(other.getPosition()) < NEIGHBOURS_RADIUS && !agent.equals(other))
                .collect(Collectors.toList());

        for (Agent neighbour : neighbours) {
            resultantNc = resultantNc.add(calculateRepulsionForce(agent.getPosition(), neighbour.getPosition(), agent.getVelocity(), agentAp, agentBp));
        }
//        Vector closestWallPosition = environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
//        Vector wallRepulsion = calculateRepulsionForce(agent.getPosition(),closestWallPosition,agent.getVelocity(),wallAp,wallBp);
        return resultantNc;
    }

    private static Vector calculateTargetDirection(Vector heuristicVelocity, Vector originalVelocity) {
        Vector heuristicWeightedVelocity = heuristicVelocity.normalize().scalarMultiply(HEURISTIC_WEIGHT);
        Vector originalWeightedVelocity = originalVelocity.normalize().scalarMultiply(1.0 - HEURISTIC_WEIGHT);
        return heuristicWeightedVelocity.add(originalWeightedVelocity).normalize();
    }


    private static Vector calculateRepulsionForce(Vector position, Vector obstacle, Vector targetVelocity, double Ap, double Bp) {
        //eij (e sub ij)
        Vector repulsionDirection = position.substract(obstacle).normalize();

        //dij (d sub ij) distance between position and otherPosition
        Double repulsionDistance = obstacle.distance(position);

//       Vector objectiveVector = objective.substract(position).normalize();
        Vector targetDirection = targetVelocity.normalize();
        //cos(0) = a.b / |a||b| (ya estan normalizados o sea |a| = |b| = 1)
        double cosineOfTheta = targetVelocity.add(position).dotMultiply(obstacle) / (targetVelocity.add(position).module() * obstacle.module());
        //eij*Ap*e^(-dij/bp)*cos(0)
        return repulsionDirection.scalarMultiply(Ap * Math.exp(-repulsionDistance / Bp) * cosineOfTheta);
    }

}
